package de.hskl.ki.services;

import de.hskl.ki.config.properties.GitProperties;
import de.hskl.ki.db.document.Project;
import de.hskl.ki.models.exceptions.AIException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class GitService {
    @Autowired
    GitProperties gitProperties;

    /**
     * Clones a repository by url
     *
     * @param repoUrl url of the repository
     * @param dir     directory to clone to
     * @return project information stub
     */
    public Project cloneRepository(String repoUrl, Path dir) {
        try {
            var git = Git.cloneRepository()
                    .setCredentialsProvider(
                            new UsernamePasswordCredentialsProvider(gitProperties.getUsername(), gitProperties.getPassword())
                    )
                    .setURI(repoUrl)
                    .setDirectory(dir.toFile())
                    .call();

            git.close();

            return new Project(String.valueOf(dir), repoUrl);
        } catch (GitAPIException e) {
            throw new AIException("Error during git clone: " + e.getMessage(), ProjectService.class);
        }
    }
}
