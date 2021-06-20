package de.hskl.ki.db.document;

import de.hskl.ki.db.document.helper.ProjectAccessInfo;
import de.hskl.ki.models.yaml.dlconfig.ConfigDLYaml;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Document
public class Project {
    @Id
    private String id;
    private String projectPath;
    private Date creationDate = new Date(System.currentTimeMillis());
    private String gitUrl;
    private ConfigDLYaml yaml;
    private Map<Integer, ProjectAccessInfo> accessInfo; // TODO: Pair
    private List<String> serviceNames;

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

    public Project(String projectPath, Date creationDate, String gitUrl, ConfigDLYaml yaml, List<String> serviceNames) {
        this.projectPath = projectPath;
        this.creationDate = creationDate;
        this.gitUrl = gitUrl;
        this.yaml = yaml;
        this.serviceNames = serviceNames;
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

    public List<String> getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(List<String> serviceNames) {
        this.serviceNames = serviceNames;
    }

    public Map<Integer, ProjectAccessInfo> getAccessInfo() {
        return accessInfo;
    }

    public void setAccessInfo(Map<Integer, ProjectAccessInfo> accessInfo) {
        this.accessInfo = accessInfo;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", projectPath='" + projectPath + '\'' +
                ", creationDate=" + creationDate +
                ", gitUrl='" + gitUrl + '\'' +
                ", yaml=" + yaml +
                ", accessInfo=" + accessInfo +
                ", serviceNames=" + serviceNames +
                '}';
    }
}
