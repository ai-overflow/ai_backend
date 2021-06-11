package de.hskl.ki.models.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProxyFormRequest {
    private String url;
    private Map<String, String> headerMap = new HashMap<>();
    private String data;
    private byte[] dataBinary;
    private String contentType;

    public ProxyFormRequest() {
    }

    public ProxyFormRequest(String url, Map<String, String> headers) {
        this.url = url;
        this.headerMap = headers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    @JsonProperty("headers")
    public void setHeaders(String header) {
        System.out.println("Called");
        ObjectMapper mapper = new ObjectMapper();
        try {
            headerMap = mapper.readValue(header, headerMap.getClass());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public byte[] getDataBinary() {
        return dataBinary;
    }

    public void setDataBinary(MultipartFile file) throws IOException {
        System.out.println(file);
        dataBinary = file.getBytes();
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "ProxyFormRequest{" +
                "url='" + url + '\'' +
                ", headerMap=" + headerMap +
                ", data='" + data + '\'' +
                ", dataBinary=" + Arrays.toString(dataBinary) +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
