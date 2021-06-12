package de.hskl.ki.db.document;

import de.hskl.ki.models.yaml.ConfigDLYaml;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class Project {
    //TODO: CreateDate, Username,
    @Id
    private String id;
    private String projectPath;
    private Date creationDate = new Date(System.currentTimeMillis());
    private String gitUrl;
    private ConfigDLYaml yaml;
    private String hostname;

    public Project(String projectPath, Date creationDate, String gitUrl) {
        this.projectPath = projectPath;
        this.creationDate = creationDate;
        this.gitUrl = gitUrl;
    }

    public Project(String projectPath, String gitUrl) {
        this.projectPath = projectPath;
        this.gitUrl = gitUrl;
    }

    public Project(String projectPath, Date creationDate, String gitUrl, ConfigDLYaml yaml) {
        this.projectPath = projectPath;
        this.creationDate = creationDate;
        this.gitUrl = gitUrl;
        this.yaml = yaml;
    }

    public Project() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public ConfigDLYaml getYaml() {
        return yaml;
    }

    public void setYaml(ConfigDLYaml yaml) {
        this.yaml = yaml;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
