package de.hskl.ki.models.yaml.dlconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YamlConnectionBody {
    private String type;
    private Object input;

    public YamlConnectionBody(String type, Object input) {
        this.type = type;
        this.input = input;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    @Override
    public String toString() {
        return "YamlConnectionBody{" +
                "type='" + type + '\'' +
                ", input=" + input +
                '}';
    }
}
