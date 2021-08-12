package de.hskl.ki.models.websocket;

import java.util.Map;

public class UploadCacheRequest {
    private Map<String, UploadCacheTopLevelInput> data;

    public Map<String, UploadCacheTopLevelInput> getData() {
        return data;
    }

    public void setData(Map<String, UploadCacheTopLevelInput> data) {
        this.data = data;
    }
}
