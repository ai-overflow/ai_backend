package de.hskl.ki.services;

import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.exceptions.AIException;
import de.hskl.ki.models.git.GitCreationRequest;
import de.hskl.ki.models.yaml.dlconfig.ConfigDLYaml;
import de.hskl.ki.services.interfaces.StorageService;
import de.hskl.ki.services.processor.SimpleYamlProcessor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class GitService {

    private final Logger logger = LoggerFactory.getLogger(GitService.class);
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
    public Project generateProject(GitCreationRequest repo) {
        Path dir = projectStorageService.generateStorageFolder();

        var projectInfo = cloneRepository(repo.getRepoUrl(), dir);
        deleteGitHistory(dir);
        try {
            var config = dlConfigYamlReader.readDlConfig(dir);
            config.ifPresent(projectInfo::setYaml);

            dockerService.processComposeFile(dir, projectInfo);
            Optional<List<String>> models = inferenceService.moveModelsToTriton(dir);
            if (models.isEmpty()) {
                logger.info("Models move failed.");
            } else {
                inferenceService.activateProject(models.get());
                projectInfo.setActiveModels(models.get());
            }

            projectRepository.save(projectInfo);
        } catch (Exception e) {
            try {
                FileUtils.deleteDirectory(dir.toFile());
            } catch (IOException ioException) {
                throw new AIException("There was an error during git clone rollback: " + e.getMessage(), GitService.class);
            }
            throw new AIException("There was an error during project generation: " + e.getMessage(), GitService.class);
        }
        return projectInfo;
    }

    /**
     * Deletes a project by Id.
     * This will also stop any associated containers and unload/remove all associated models from triton
     *
     * @param projectId project id
     */
    public void deleteProject(String projectId) {
        containerProxyService.stopContainer(projectId);

        var project = projectRepository.getProjectById(projectId);

        inferenceService.deactivateProject(project.getActiveModels());
        inferenceService.deleteModelFromTritonFolder(project.getActiveModels());

        var p = Paths.get(project.getProjectPath());
        try {
            FileUtils.deleteDirectory(p.toFile());
            projectRepository.delete(project);
        } catch (IOException e) {
            throw new AIException("Unable to delete project files", GitService.class);
        }
    }

    /**
     * Clones a repository by url
     *
     * @param repoUrl url of the repository
     * @param dir     directory to clone to
     * @return project information stub
     */
    private Project cloneRepository(String repoUrl, Path dir)  {
        try {
            var git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(dir.toFile())
                    .call();

            git.close();

            return new Project(String.valueOf(dir), repoUrl);
        } catch (GitAPIException e) {
            throw new AIException("Error during git clone: " + e.getMessage(), GitService.class);
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
            throw new AIException("Unable to delete Git history", GitService.class);
        }
    }
}
