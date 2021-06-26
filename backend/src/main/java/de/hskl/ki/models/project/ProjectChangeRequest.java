package de.hskl.ki.models.project;

public class ProjectChangeRequest {
    private String name;
    private String description;
    private String repoUrl;

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

    @Override
    public String toString() {
        return "ProjectChangeRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", gitUrl='" + repoUrl + '\'' +
                '}';
    }
}
