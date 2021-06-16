package de.hskl.ki.models.yaml.dlconfig;

public class YamlInput {
    private String label;
    private String type;
    private Object values;

    public YamlInput(String label, String type, Object values) {
        this.label = label;
        this.type = type;
        this.values = values;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValues() {
        return values;
    }

    public void setValues(Object values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "YamlInput{" +
                "label='" + label + '\'' +
                ", type='" + type + '\'' +
                ", values=" + values +
                '}';
    }
}
