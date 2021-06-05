package de.hskl.ki.resource;

import de.hskl.ki.db.document.Projects;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.git.GitCreationRequest;
import de.hskl.ki.models.git.GitCreationResponse;
import de.hskl.ki.services.interfaces.StorageService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/git/")
public class GitResource {

    @Autowired
    StorageService projectStorageService;

    @Autowired
    private ProjectRepository projectRepository;

    @PostMapping
    public ResponseEntity<?> cloneRepo(@RequestBody GitCreationRequest repo) throws GitAPIException {
        Optional<Path> dir = projectStorageService.generateStorageFolder();
        if (dir.isPresent()) {
            var projectInfo = cloneRepository(repo.getRepoUrl(), dir.get());
            deleteGitHistory(dir.get());

            return ResponseEntity.ok(projectInfo);
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    private Projects cloneRepository(String repoUrl, Path dir) throws GitAPIException {
        Git git = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(dir.toFile())
                .call();
        git.getRepository().close();
        git.close();

        var projectInfo = new Projects(String.valueOf(dir), repoUrl);
        projectRepository.save(projectInfo);
        return projectInfo;
        // TODO: Cleanup if anything fails
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
}
