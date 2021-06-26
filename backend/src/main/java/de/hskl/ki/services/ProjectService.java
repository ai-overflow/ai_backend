package de.hskl.ki.services;

import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.exceptions.AIException;
import de.hskl.ki.models.project.ProjectChangeRequest;
import de.hskl.ki.models.yaml.dlconfig.ConfigDLYaml;
import de.hskl.ki.services.interfaces.StorageService;
import de.hskl.ki.services.processor.SimpleYamlProcessor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * This method will download a Project from a given Git location.
     * This will also change the project structure according to {@link DockerService#processComposeFile(Path, Project)}.
     *
     * @param repo Project retrieval information
     * @return Project information
     */
    public Project generateProject(String repo) {
        Path dir = projectStorageService.generateStorageFolder();

        var projectInfo = processProject(repo, dir);
        projectRepository.save(projectInfo);
        return projectInfo;
    }

    public Project reloadProject(String projectId, String repo) {
        var project = getProjectById(projectId);
        deleteProjectFolder(project);
        var newProject = processProject(repo, Path.of(project.getProjectPath()));
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

        inferenceService.deactivateModel(project.getActiveModels());
        inferenceService.deleteModelFromTritonFolder(project.getActiveModels());

        var p = Paths.get(project.getProjectPath());
        try {
            FileUtils.deleteDirectory(p.toFile());
        } catch (IOException e) {
            throw new AIException("Unable to delete project files", ProjectService.class);
        }
    }

    private Project processProject(String repo, Path dir) {
        var projectInfo = cloneRepository(repo, dir);
        deleteGitHistory(dir);
        try {
            var config = dlConfigYamlReader.readDlConfig(dir);
            config.ifPresent(projectInfo::setYaml);

            dockerService.processComposeFile(dir, projectInfo);
            Optional<List<String>> models = inferenceService.moveModelsToTriton(dir);
            if (models.isEmpty()) {
                logger.info("Models move failed.");
            } else {
                inferenceService.activateModel(models.get());
                projectInfo.setActiveModels(models.get());
            }
        } catch (Exception e) {
            try {
                FileUtils.deleteDirectory(dir.toFile());
            } catch (IOException ioException) {
                throw new AIException("There was an error during git clone rollback: " + e.getMessage(), ProjectService.class);
            }
            throw new AIException("There was an error during project generation: " + e.getMessage(), ProjectService.class);
        }
        return projectInfo;
    }

    /**
     * Clones a repository by url
     *
     * @param repoUrl url of the repository
     * @param dir     directory to clone to
     * @return project information stub
     */
    private Project cloneRepository(String repoUrl, Path dir) {
        try {
            var git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(dir.toFile())
                    .call();

            git.close();

            return new Project(String.valueOf(dir), repoUrl);
        } catch (GitAPIException e) {
            throw new AIException("Error during git clone: " + e.getMessage(), ProjectService.class);
        }
    }

    /**
     * Delete .git history of project
     *
     * @param dir project directory
     */
    private void deleteGitHistory(Path dir) {
        try {
            FileUtils.deleteDirectory(new File(String.valueOf(dir.resolve(".git"))));
        } catch (IOException e) {
            throw new AIException("Unable to delete Git history", ProjectService.class);
        }
    }

    public void updateProject(String id, ProjectChangeRequest changes) {
        Project projectValue = getProjectById(id);

        if(projectValue.getGitUrl() != null &&
                !projectValue.getGitUrl().isEmpty() &&
                !projectValue.getGitUrl().equals(changes.getRepoUrl())) {
            projectValue = reloadProject(id, changes.getRepoUrl());
        }

        var yaml = projectValue.getYaml();
        yaml.setName(changes.getName());
        yaml.setDescription(changes.getDescription());
        projectValue.setYaml(yaml);
        projectRepository.save(projectValue);
    }

    public void removeTritonModels(String id) {
        Project project = getProjectById(id);

        inferenceService.deactivateModel(project.getActiveModels());
        inferenceService.deleteModelFromTritonFolder(project.getActiveModels());

        var yaml = project.getYaml();
        yaml.setTritonEnabled(false);
        project.setYaml(yaml);

        project.setActiveModels(new ArrayList<>());
        projectRepository.save(project);
    }

    public void activateAllModels() {
        projectRepository.findAll().forEach(project -> inferenceService.activateModel(project.getActiveModels()));

    }

    private Project getProjectById(String id) {
        var project = projectRepository.findById(id);
        if (project.isEmpty()) {
            throw new AIException("Unable to find project by ID", ProjectService.class);
        }
        return project.get();
    }
}
