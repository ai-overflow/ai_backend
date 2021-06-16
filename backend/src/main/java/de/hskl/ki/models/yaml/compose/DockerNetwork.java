package de.hskl.ki.models.yaml.compose;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DockerNetwork {
    private boolean external;
    private String name;

    public DockerNetwork(boolean external, String name) {
        this.external = external;
        this.name = name;
    }

    public DockerNetwork(boolean external) {
        this.external = external;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DockerNetwork{" +
                "external=" + external +
                ", name='" + name + '\'' +
                '}';
    }
}
