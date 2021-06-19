package de.hskl.ki.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.docker.manager")
public class DockerManagerProperties {
    private String containerHost = "ERROR";
    private Integer containerPort = -1;

    public String getContainerHost() {
        return containerHost;
    }

    public void setContainerHost(String containerHost) {
        this.containerHost = containerHost;
    }

    public Integer getContainerPort() {
        return containerPort;
    }

    public void setContainerPort(Integer containerPort) {
        this.containerPort = containerPort;
    }
}
