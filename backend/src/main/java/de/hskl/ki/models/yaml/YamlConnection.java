package de.hskl.ki.models.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

public class YamlConnection {
    private Integer port;
    private String protocol;
    private String path;
    private String method;
    private Map<String, String> params;
    private Map<String, String> headers;
    private YamlConnectionBody body;


    public YamlConnection(Integer port, String protocol, String path, String method, Map<String, String> params, Map<String, String> headers, YamlConnectionBody body) {
        this.port = port;
        this.protocol = protocol;
        this.path = path;
        this.method = method;
        this.params = params;
        this.headers = headers;
        this.body = body;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public YamlConnectionBody getBody() {
        return body;
    }

    public void setBody(YamlConnectionBody body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "YamlConnection{" +
                "port=" + port +
                ", protocol='" + protocol + '\'' +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", params=" + params +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }
}
