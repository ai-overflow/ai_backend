package de.hskl.ki.models.yaml.compose;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Service {
    private String image;
    private Object build;
    private List<String> ports;
    private List<String> expose;
    private List<Object> volumes;
    private String command;
    private List<String> environment;
    private String restart;
    private String hostname;
    private List<String> networks;
    private final Map<String, Object> ulimits;

    public Service(String image, Object build, List<String> ports, List<String> expose, List<Object> volumes, String command, List<String> environment, String restart, String hostname, List<String> networks, Map<String, Object> ulimits) {
        this.image = image;
        this.build = build;
        this.ports = ports;
        this.expose = expose;
        this.volumes = volumes;
        this.command = command;
        this.environment = environment;
        this.restart = restart;
        this.hostname = hostname;
        this.networks = networks;
        this.ulimits = ulimits;
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

    public List<String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(List<String> environment) {
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

    public List<String> getNetworks() {
        return networks;
    }

    public void setNetworks(List<String> networks) {
        this.networks = networks;
    }

    public List<String> getExpose() {
        return expose;
    }

    public void setExpose(List<String> expose) {
        this.expose = expose;
    }

    public Map<String, Object> getUlimits() {
        return ulimits;
    }


    @Override
    public String toString() {
        return "Service{" +
                "image='" + image + '\'' +
                ", build=" + build +
                ", ports=" + ports +
                ", expose=" + expose +
                ", volumes=" + volumes +
                ", command='" + command + '\'' +
                ", environment=" + environment +
                ", restart='" + restart + '\'' +
                ", hostname='" + hostname + '\'' +
                ", networks=" + networks +
                ", ulimits=" + ulimits +
                '}';
    }
}
