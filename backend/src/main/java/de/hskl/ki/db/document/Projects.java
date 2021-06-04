package de.hskl.ki.db.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Projects {
    @Id
    private Integer id;
    private String gitUrl;

    public Projects(Integer id, String gitUrl) {
        this.id = id;
        this.gitUrl = gitUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }
}
