package de.hskl.ki.models.websocket;

public class UploadCacheNameElement {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UploadCacheNameElement{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
