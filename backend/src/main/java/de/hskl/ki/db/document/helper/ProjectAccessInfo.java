package de.hskl.ki.db.document.helper;


public class ProjectAccessInfo {
    private String hostname;
    private Integer port;

    public ProjectAccessInfo(String hostname, Integer port) {
        this.hostname = hostname;
        this.port = port;
    }

    public ProjectAccessInfo() {
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "ProjectAccessInfo{" +
                "hostname='" + hostname + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
