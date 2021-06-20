package de.hskl.ki.services;

import de.hskl.ki.config.properties.SpringProperties;
import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.proxy.ProxyFormRequest;
import de.hskl.ki.models.proxy.RequestMethods;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Service
public class ProxyService {
    private final Logger logger = LoggerFactory.getLogger(ProxyService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SpringProperties springProperties;

    // TODO: FormData, Image Conversion, HTML Sanitizing
    public Optional<byte[]> proxyRequest(ProxyFormRequest formRequest) {
        var project = projectRepository.findById(formRequest.getId());
        if (project.isEmpty())
            return Optional.empty();

        URL url;
        try {
            URL tmpUrl = new URL(formRequest.getUrl());
            if (!tmpUrl.getHost().equals("{{internal.HOST_URL}}")) {
                return Optional.empty();
            }
            url = new URL(parseUrl(tmpUrl, project.get()));
        } catch (MalformedURLException e) {
            return Optional.empty();
        }

        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(formRequest.getMethod().name());
            for (Map.Entry<String, String> entry : formRequest.getHeaderMap().entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            con.setRequestProperty("Content-Type", formRequest.getContentType());
            con.setInstanceFollowRedirects(true);
            con.setDoOutput(true);

            if (!formRequest.getMethod().equals(RequestMethods.GET)) {
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = null;
                    if (formRequest.getDataBinary() != null) {
                        input = formRequest.getDataBinary();
                    } else if (formRequest.getData() != null) {
                        input = formRequest.getData().getBytes(StandardCharsets.UTF_8);
                    }

                    if (input != null) {
                        os.write(input, 0, input.length);
                    }
                }
            }
            InputStream responseStream = con.getInputStream();
            return Optional.of(IOUtils.toByteArray(responseStream));
        } catch (ConnectException e) {
            logger.warn("Failed to connect: " + parseUrl(url, project.get()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private String parseUrl(URL url, Project project) {
        // TODO: Load hostname from DB
        if (springProperties.hasEnvironment("dev")) {
            return url.toString().replace("{{internal.HOST_URL}}", "localhost");
        } else {
            var uriBuilder = UriComponentsBuilder.fromHttpUrl(url.toString());
            return uriBuilder
                    .host(project.getAccessInfo().get(url.getPort()).getHostname())
                    .port(project.getAccessInfo().get(url.getPort()).getPort())
                    .toUriString();
        }
    }
}
