package de.hskl.ki.models.yaml;

public class YamlOutput {
    private String label;
    private String type;
    private YamlOutputRepeat repeat;
    private Object format;

    public YamlOutput(String label, String type, YamlOutputRepeat repeat, Object format) {
        this.label = label;
        this.type = type;
        this.repeat = repeat;
        this.format = format;
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

    public YamlOutputRepeat getRepeat() {
        return repeat;
    }

    public void setRepeat(YamlOutputRepeat repeat) {
        this.repeat = repeat;
    }

    public Object getFormat() {
        return format;
    }

    public void setFormat(Object format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "YamlOutput{" +
                "label='" + label + '\'' +
                ", type='" + type + '\'' +
                ", repeat=" + repeat +
                ", format=" + format +
                '}';
    }
}
