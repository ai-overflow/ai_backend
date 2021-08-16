package de.hskl.ki.models.websocket;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class UploadCacheTopLevelInput {
    private MultipartFile data;
    private List<UploadCacheNameElement> names;

    public MultipartFile  getData() {
        return data;
    }

    public void setData(MultipartFile  data) {
        this.data = data;
    }

    public List<UploadCacheNameElement> getNames() {
        return names;
    }

    public void setNames(List<UploadCacheNameElement> names) {
        this.names = names;
    }
}
