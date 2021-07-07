package de.hskl.ki.models.project;

public class ProjectChangeRequest {
    private String name;
    private String description;
    private String repoUrl;
    private boolean reloadFromGit;

    public ProjectChangeRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public boolean isReloadFromGit() {
        return reloadFromGit;
    }

    public void setReloadFromGit(boolean reloadFromGit) {
        this.reloadFromGit = reloadFromGit;
    }

    @Override
    public String toString() {
        return "ProjectChangeRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", repoUrl='" + repoUrl + '\'' +
                ", reloadFromGit=" + reloadFromGit +
                '}';
    }
}
