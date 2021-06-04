package de.hskl.ki.models.git;

public class GitCreationRequest {
    private String repoUrl;

    public GitCreationRequest() {
    }

    public GitCreationRequest(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }
}
