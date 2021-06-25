package de.hskl.ki.models.project;

public class ProjectCreationResponse {

    private String repoPath;

    public ProjectCreationResponse(String repoPath) {
        this.repoPath = repoPath;
    }

    public String getRepoPath() {
        return repoPath;
    }

    public void setRepoPath(String repoPath) {
        this.repoPath = repoPath;
    }

}
