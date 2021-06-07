package de.hskl.ki.models.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigDLYaml {
    private String entryPoint;
    private String name;
    private String description;
    private List<String> authors;
    private Map<String, YamlConnection> connection;
    private Map<String, YamlInput> input;
    private Map<String, YamlOutput> output;

    public ConfigDLYaml(String entryPoint, String name, String description, List<String> authors, Map<String, YamlConnection> connection, Map<String, YamlInput> input, Map<String, YamlOutput> output) {
        this.entryPoint = entryPoint;
        this.name = name;
        this.description = description;
        this.authors = authors;
        this.connection = connection;
        this.input = input;
        this.output = output;
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

    public Map<String, YamlConnection> getConnection() {
        return connection;
    }

    public void setConnection(Map<String, YamlConnection> connection) {
        this.connection = connection;
    }

    public Map<String, YamlInput> getInput() {
        return input;
    }

    public void setInput(Map<String, YamlInput> input) {
        this.input = input;
    }

    public Map<String, YamlOutput> getOutput() {
        return output;
    }

    public void setOutput(Map<String, YamlOutput> output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return "ConfigDLYaml{" +
                "entryPoint='" + entryPoint + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", authors=" + authors +
                ", connection=" + connection +
                ", input=" + input +
                ", output=" + output +
                '}';
    }
}
