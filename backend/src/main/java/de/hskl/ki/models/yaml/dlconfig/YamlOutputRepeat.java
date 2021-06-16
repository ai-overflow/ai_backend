package de.hskl.ki.models.yaml.dlconfig;

public class YamlOutputRepeat {
    private String iterator;
    private String title;

    public YamlOutputRepeat(String iterator, String title) {
        this.iterator = iterator;
        this.title = title;
    }

    public String getIterator() {
        return iterator;
    }

    public void setIterator(String iterator) {
        this.iterator = iterator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "YamlOutputRepeat{" +
                "iterator='" + iterator + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
