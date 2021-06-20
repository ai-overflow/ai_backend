package de.hskl.ki.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.project")
public class ProjectProperties {
    private String directory = "/projects";
    private String hostDir = "/";
    private String internalNetworkName = "dl_project_network";
    private String projectContainerPrefix = "project_";

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getHostDir() {
        return hostDir;
    }

    public void setHostDir(String hostDir) {
        this.hostDir = hostDir;
    }

    public String getInternalNetworkName() {
        return internalNetworkName;
    }

    public void setInternalNetworkName(String internalNetworkName) {
        this.internalNetworkName = internalNetworkName;
    }

    public String getProjectContainerPrefix() {
        return projectContainerPrefix;
    }

    public void setProjectContainerPrefix(String projectContainerPrefix) {
        this.projectContainerPrefix = projectContainerPrefix;
    }
}
