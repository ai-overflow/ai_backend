package de.hskl.ki.models.yaml.compose;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DockerComposeYaml {
    private String version;
    private Map<String, Service> services;
    private Map<String, Object> secrets;
    private Map<String, DockerNetwork> networks;
    private Map<String, Object> volumes;

    public DockerComposeYaml(String version, Map<String, Service> services, Map<String, Object> secrets, Map<String, DockerNetwork> networks, Map<String, Object> volumes) {
        this.version = version;
        this.services = services;
        this.secrets = secrets;
        this.networks = networks;
        this.volumes = volumes;
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

    public Map<String, Object> getSecrets() {
        return secrets;
    }

    public void setSecrets(Map<String, Object> secrets) {
        this.secrets = secrets;
    }

    public Map<String, DockerNetwork> getNetworks() {
        return networks;
    }

    public void setNetworks(Map<String, DockerNetwork> networks) {
        this.networks = networks;
    }

    public Map<String, Object> getVolumes() {
        return volumes;
    }

    public void setVolumes(Map<String, Object> volumes) {
        this.volumes = volumes;
    }

    @Override
    public String toString() {
        return "DockerComposeYaml{" +
                "version='" + version + '\'' +
                ", services=" + services +
                ", secrets=" + secrets +
                ", networks=" + networks +
                ", volumes=" + volumes +
                '}';
    }
}
