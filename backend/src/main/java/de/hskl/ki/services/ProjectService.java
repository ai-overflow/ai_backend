package de.hskl.ki.services;

import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.exceptions.AIException;
import de.hskl.ki.models.project.ProjectChangeRequest;
import de.hskl.ki.models.yaml.dlconfig.ConfigDLYaml;
import de.hskl.ki.services.interfaces.StorageService;
import de.hskl.ki.services.processor.SimpleYamlProcessor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProjectService {

    private final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private final SimpleYamlProcessor<ConfigDLYaml> dlConfigYamlReader = new SimpleYamlProcessor<>(ConfigDLYaml.class);

    @Autowired
    private StorageService projectStorageService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private DockerService dockerService;
    @Autowired
    private InferenceService inferenceService;
    @Autowired
    private ContainerProxyService containerProxyService;
    @Autowired
    private GitService gitService;

    /**
     * This method will download a Project from a given Git location.
     * This will also change the project structure according to {@link DockerService#processComposeFile(Path, Project)}.
     *
     * @param repo Project retrieval information
     * @return Project information
     */
    public Project generateProject(String repo) {
        Path dir = projectStorageService.generateStorageFolder();
        var projectInfo = gitService.cloneRepository(repo, dir);

        processProject(projectInfo);
        projectRepository.save(projectInfo);
        return projectInfo;
    }

    public Project generateProject(Path projectDir) {
        var projectInfo = new Project(String.valueOf(projectDir), "upload");
        processProject(projectInfo);
        projectRepository.save(projectInfo);
        return projectInfo;
    }

    public Project reloadProject(String projectId, String repo) {
        var project = getProjectById(projectId);
        deleteProjectFolder(project);
        var projectInfo = gitService.cloneRepository(repo, Path.of(project.getProjectPath()));
        var newProject = processProject(projectInfo);
        newProject.setId(project.getId());
        return projectRepository.save(newProject);
    }

    /**
     * Deletes a project by Id.
     * This will also stop any associated containers and unload/remove all associated models from triton
     *
     * @param projectId project id
     */
    public void deleteProject(String projectId) {
        var project = projectRepository.getProjectById(projectId);
        deleteProjectFolder(project);
        projectRepository.delete(project);
    }

    private void deleteProjectFolder(Project project) {
        containerProxyService.stopContainer(project.getId());

        if(!project.getActiveModels().isEmpty()) {
            inferenceService.deactivateModel(project.getActiveModels());
            inferenceService.deleteModelFromTritonFolder(project.getActiveModels());
        }

        var p = Paths.get(project.getProjectPath());
        try {
            FileUtils.deleteDirectory(p.toFile());
        } catch (IOException e) {
            throw new AIException("Unable to delete project files", ProjectService.class);
        }
    }

    private Project processProject(Project projectInfo) {
        var dir = Path.of(projectInfo.getProjectPath());
        gitService.deleteGitHistory(dir);

        try {
            moveConfigToRoot(dir);

            var config = dlConfigYamlReader.readDlConfig(dir);
            config.ifPresent(projectInfo::setYaml);

            dockerService.processComposeFile(dir, projectInfo);
            Optional<List<String>> models = inferenceService.moveModelsToTriton(dir);
            if (models.isEmpty()) {
                logger.info("Models move failed.");
            } else {
                if(activateModelOrDelete(models.get()))
                    projectInfo.setActiveModels(models.get());
            }
        } catch (AIException e) {
            deleteProjectAfterException(dir, e.getMessage());
            throw new AIException("Error in Project " + dir + ": " + e.getMessage(), ProjectService.class);
        } catch (Exception e) {
            deleteProjectAfterException(dir, e.getMessage());
            throw new AIException("There was an error during project generation: " + e.getMessage(), ProjectService.class);
        }
        return projectInfo;
    }

    private boolean activateModelOrDelete(List<String> models) {
        boolean success = false;
        try {
            inferenceService.activateModel(models);
            success = true;
        } catch(AIException e) {
            inferenceService.deleteModelFromTritonFolder(models);
        }
        return success;
    }

    private void deleteProjectAfterException(Path dir, String message) {
        try {
            FileUtils.deleteDirectory(dir.toFile());
        } catch (IOException ioException) {
            throw new AIException("There was an error during git clone rollback: " + message, ProjectService.class);
        }
    }

    private void moveConfigToRoot(Path dir) throws IOException {
        var configCandidates = dlConfigYamlReader.findDlConfigFolder(dir);
        if (configCandidates.size() > 1) {
            throw new AIException("Multiple locations of config files found: " + configCandidates, ProjectService.class);
        }
        var configFilePath = configCandidates.stream().findFirst();
        if (configCandidates.isEmpty()) {
            throw new AIException("Missing config file in project", ProjectService.class);
        }
        var newProjectRoot = configFilePath.get().getParent();

        if (!newProjectRoot.equals(dir)) {
            Utility.replaceRootWithSubfolder(dir, newProjectRoot);
        }
    }

    public void updateProject(String id, ProjectChangeRequest changes) {
        var project = getProjectById(id);

        if (project.getGitUrl() != null &&
                !project.getGitUrl().isEmpty() &&
                !project.getGitUrl().equals(changes.getRepoUrl())) {
            project = reloadProject(id, changes.getRepoUrl());
        }

        var yaml = project.getYaml();
        yaml.setName(changes.getName());
        yaml.setDescription(changes.getDescription());
        project.setYaml(yaml);
        projectRepository.save(project);
    }

    public void removeTritonModels(String id) {
        var project = getProjectById(id);

        inferenceService.deactivateModel(project.getActiveModels());
        inferenceService.deleteModelFromTritonFolder(project.getActiveModels());

        var yaml = project.getYaml();
        yaml.setTritonEnabled(false);
        project.setYaml(yaml);

        project.setActiveModels(new ArrayList<>());
        projectRepository.save(project);
    }

    public void activateAllModels() {
        projectRepository.findAll().stream()
                .map(Project::getActiveModels)
                .filter(Objects::nonNull)
                .forEach(models -> inferenceService.activateModel(models));

    }

    private Project getProjectById(String id) {
        var project = projectRepository.findById(id);
        if (project.isEmpty()) {
            throw new AIException("Unable to find project by ID", ProjectService.class);
        }
        return project.get();
    }
}
