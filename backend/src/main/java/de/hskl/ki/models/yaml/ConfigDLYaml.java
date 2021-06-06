package de.hskl.ki.models.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigDLYaml {
    private String entryPoint;
    private String name;
    private String description;
    private List<String> authors;

    public ConfigDLYaml(String entryPoint, String name, String description, List<String> authors) {
        this.entryPoint = entryPoint;
        this.name = name;
        this.description = description;
        this.authors = authors;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        return "ConfigDLYaml{" +
                "entryPoint='" + entryPoint + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", authors=" + authors +
                '}';
    }
}
