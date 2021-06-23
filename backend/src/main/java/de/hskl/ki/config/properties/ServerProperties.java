package de.hskl.ki.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.server")
public class ServerProperties {
    private String url = "http://localhost:8080";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
