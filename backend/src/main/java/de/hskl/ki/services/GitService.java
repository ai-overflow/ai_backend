package de.hskl.ki.services;

import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.git.GitCreationRequest;
import de.hskl.ki.models.yaml.compose.DockerComposeYaml;
import de.hskl.ki.models.yaml.dlconfig.ConfigDLYaml;
import de.hskl.ki.services.interfaces.StorageService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
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

    @Autowired
    StorageService projectStorageService;

    @Autowired
    private ProjectRepository projectRepository;

    private final SimpleYamlReader<ConfigDLYaml> dlConfigYamlReader = new SimpleYamlReader<>(ConfigDLYaml.class);
    private final SimpleYamlReader<DockerComposeYaml> composeYamlReader = new SimpleYamlReader<>(DockerComposeYaml.class);

    public Optional<Project> generateProject(GitCreationRequest repo) throws GitAPIException, IOException {
        Optional<Path> dir = projectStorageService.generateStorageFolder();
        if (dir.isPresent()) {
            var projectDir = dir.get();
            var projectInfo = cloneRepository(repo.getRepoUrl(), projectDir);
            deleteGitHistory(projectDir);
            try {
                var config = dlConfigYamlReader.read(projectDir);
                config.ifPresent(projectInfo::setYaml);

                var composeConfig = composeYamlReader.read(projectDir, "docker-compose", List.of("yaml", "yml"));
                composeConfig.ifPresent(System.out::println);

                projectRepository.save(projectInfo);
            } catch (Exception e) {
                FileUtils.deleteDirectory(projectDir.toFile());
                throw e;
            }
            return Optional.of(projectInfo);
        }
        return Optional.empty();
    }

    private Project cloneRepository(String repoUrl, Path dir) throws GitAPIException {
        Git git = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(dir.toFile())
                .call();

        git.close();

        return new Project(String.valueOf(dir), repoUrl);
    }

    private void deleteGitHistory(Path dir) {
        try {
            FileUtils.deleteDirectory(new File(String.valueOf(dir.resolve(".git"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RevCommit getLatestCommit(Git git) throws GitAPIException {
        RevCommit latestCommit = null;
        try {
            List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
            RevWalk walk = new RevWalk(git.getRepository());
            for (Ref branch : branches) {
                RevCommit commit = walk.parseCommit(branch.getObjectId());
                if (latestCommit == null || commit.getAuthorIdent().getWhen().compareTo(
                        latestCommit.getAuthorIdent().getWhen()) > 0)
                    latestCommit = commit;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latestCommit;
    }

    public boolean deleteProject(String projectId) {
        Project project = projectRepository.getProjectById(projectId);
        Path p = Paths.get(project.getProjectPath());
        try {
            FileUtils.deleteDirectory(p.toFile());
            projectRepository.delete(project);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
