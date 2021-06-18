package de.hskl.ki.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.project")
public class ProjectProperties {
    private String directory = "/projects";
    private String hostDir = "/";

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
}
