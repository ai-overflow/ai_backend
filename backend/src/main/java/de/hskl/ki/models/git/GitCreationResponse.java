package de.hskl.ki.models.git;

import java.util.Date;

public class GitCreationResponse {

    private String repoPath;

    public GitCreationResponse(String repoPath) {
        this.repoPath = repoPath;
    }

    public String getRepoPath() {
        return repoPath;
    }

    public void setRepoPath(String repoPath) {
        this.repoPath = repoPath;
    }

}
