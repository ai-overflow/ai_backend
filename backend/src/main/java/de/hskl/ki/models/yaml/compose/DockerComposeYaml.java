package de.hskl.ki.models.yaml.compose;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DockerComposeYaml {
    private String version;
    private Map<String, Service> services;

    public DockerComposeYaml(String version, Map<String, Service> services) {
        this.version = version;
        this.services = services;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Service> getServices() {
        return services;
    }

    public void setServices(Map<String, Service> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return "DockerComposeYaml{" +
                "version='" + version + '\'' +
                ", services=" + services +
                '}';
    }
}
