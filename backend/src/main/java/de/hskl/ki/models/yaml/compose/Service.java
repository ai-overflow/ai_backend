package de.hskl.ki.models.yaml.compose;

import java.util.List;
import java.util.Map;

public class Service {
    private String image;
    private Object build;
    private List<String> ports;
    private List<Object> volumes;
    private String command;
    private Map<String, String> environment;
    private String restart;
    private String hostname;
    private Map<String, Object> networks;

    public Service(String image, Object build, List<String> ports, List<Object> volumes, String command, Map<String, String> environment, String restart, String hostname, Map<String, Object> networks) {
        this.image = image;
        this.build = build;
        this.ports = ports;
        this.volumes = volumes;
        this.command = command;
        this.environment = environment;
        this.restart = restart;
        this.hostname = hostname;
        this.networks = networks;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Object getBuild() {
        return build;
    }

    public void setBuild(Object build) {
        this.build = build;
    }

    public List<String> getPorts() {
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
    }

    public List<Object> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Object> volumes) {
        this.volumes = volumes;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

    public String getRestart() {
        return restart;
    }

    public void setRestart(String restart) {
        this.restart = restart;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Map<String, Object> getNetworks() {
        return networks;
    }

    public void setNetworks(Map<String, Object> networks) {
        this.networks = networks;
    }

    @Override
    public String toString() {
        return "Service{" +
                "image='" + image + '\'' +
                ", build=" + build +
                ", ports=" + ports +
                ", volumes=" + volumes +
                ", command='" + command + '\'' +
                ", environment=" + environment +
                ", restart='" + restart + '\'' +
                ", hostname='" + hostname + '\'' +
                ", networks=" + networks +
                '}';
    }
}
