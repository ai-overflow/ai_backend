package de.hskl.ki.resource;

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

    @PostMapping
    public ResponseEntity<?> cloneRepo(@RequestBody GitCreationRequest repo) throws GitAPIException {
        Optional<Path> dir = projectStorageService.generateStorageFolder();
        if(dir.isPresent()) {
            cloneRepository(repo.getRepoUrl(), dir.get());
            deleteGitHistory(dir.get());

            return ResponseEntity.ok(new GitCreationResponse(dir.get().toString()));
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    private void cloneRepository(String repoUrl, Path dir) throws GitAPIException {
        Git git = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(dir.toFile())
                .call();
        git.getRepository().close();
        git.close();
    }

    //TODO: This doesn't work
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
