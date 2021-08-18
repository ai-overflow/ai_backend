package de.hskl.ki.models.websocket;

import java.util.List;

public class UploadCacheTopLevelInput {
    private List<UploadCacheNameElement> aliasList;
    private String base64Data;

    public String getBase64Data() {
        return base64Data;
    }

    public void setBase64Data(String base64Data) {
        this.base64Data = base64Data;
    }

    public List<UploadCacheNameElement> getAliasList() {
        return aliasList;
    }

    public void setAliasList(List<UploadCacheNameElement> aliasList) {
        this.aliasList = aliasList;
    }

    @Override
    public String toString() {
        return "UploadCacheTopLevelInput{" +
                "names=" + aliasList +
                ", base64Data='" + base64Data.substring(0, 100) + '\'' +
                '}';
    }
}
