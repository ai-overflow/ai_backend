package de.hskl.ki.models.project;

public class ProjectCreationRequest {
    private String repoUrl;

    public ProjectCreationRequest() {
    }

    public ProjectCreationRequest(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }
}
