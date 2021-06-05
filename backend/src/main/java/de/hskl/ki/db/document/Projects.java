package de.hskl.ki.db.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.nio.file.Path;
import java.util.Date;

@Document
public class Projects {
    //TODO: CreateDate, Username,
    @Id
    private String id;
    private String projectPath;
    private Date creationDate = new Date(System.currentTimeMillis());
    private String gitUrl;

    public Projects(String projectPath, Date creationDate, String gitUrl) {
        this.projectPath = projectPath;
        this.creationDate = creationDate;
        this.gitUrl = gitUrl;
    }

    public Projects(String projectPath, String gitUrl) {
        this.projectPath = projectPath;
        this.gitUrl = gitUrl;
    }

    public Projects() {
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
}
