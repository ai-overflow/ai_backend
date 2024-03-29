package de.hskl.ki.services;

import de.hskl.ki.config.properties.SpringProperties;
import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.document.helper.StatisticsEntry;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.db.repository.StatisticsRepository;
import de.hskl.ki.models.exceptions.AIException;
import de.hskl.ki.models.proxy.ProxyFormRequest;
import de.hskl.ki.models.proxy.RequestMethods;
import de.hskl.ki.models.yaml.dlconfig.YamlConnection;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProxyService {
    private final Logger logger = LoggerFactory.getLogger(ProxyService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private SpringProperties springProperties;

    @Autowired
    private UploadCacheService uploadCacheService;

    // TODO: FormData, Image Conversion, HTML Sanitizing
    public byte[] proxyRequest(ProxyFormRequest formRequest) {
        var project = projectRepository.findById(formRequest.getId());
        if (project.isEmpty()) {
            throw new AIException("Unable to find related Project", ProxyService.class);
        }

        URL url;
        try {
            var tmpUrl = new URL(formRequest.getUrl());
            if (!tmpUrl.getHost().equals("{{internal.HOST_URL}}")) {
                throw new AIException("Malformed Request", ProxyService.class);
            }

            var connection = project.get()
                    .getYaml()
                    .getConnection()
                    .values()
                    .stream()
                    .filter(e -> e.getPort().equals(tmpUrl.getPort()))
                    .findFirst();

            if (connection.isEmpty() ||
                    !connection.get().getMethod().equalsIgnoreCase(formRequest.getMethod().toString())) {
                throw new AIException("Request not found", ProxyService.class);
            }

            if(formRequest.getData() != null)
                fillProjectData(formRequest, connection.get());

            url = new URL(parseUrl(tmpUrl, project.get()));
        } catch (MalformedURLException e) {
            throw new AIException("Malformed URL", ProxyService.class);
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
                try (var os = con.getOutputStream()) {
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
            var start = Instant.now();
            try (var responseStream = con.getInputStream()) {
                var stop = Instant.now();

                addToStatistics(project.get(), start, stop);
                return IOUtils.toByteArray(responseStream);
            }
        } catch (ConnectException e) {
            logger.warn("Failed to connect: {}", parseUrl(url, project.get()));
            throw new AIException("Failed to connect: " + parseUrl(url, project.get()), ProxyService.class);
        } catch (IOException e) {
            throw new AIException("Unable to request content from proxy: " + e.getMessage(), ProxyService.class);
        }
    }

    private void fillProjectData(ProxyFormRequest formRequest, YamlConnection connection) {
        assert(formRequest.getData() != null);

        //TODO: move up
        String pattern = "\\{\\{input\\.(.*?)}}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(formRequest.getData().toLowerCase());
        if (m.find()) {
            var varName = m.group(1);
            var cacheRequest = uploadCacheService.getSendCache().get(formRequest.getCacheId());
            cacheRequest.getData().forEach((k, v) -> {
                var result = v.getAliasList().stream().filter(e -> e.getId().equals(formRequest.getId())).findFirst();
                if (result.isPresent()) {
                    if (result.get().getName().equalsIgnoreCase(varName)) {
                        var data = v.getBase64Data();
                        if(connection.getBody().getType().equalsIgnoreCase("raw")) {
                            // TODO: Replace input.ABC with base64
                            System.out.println("data: " + data.substring(0, 100));
                        } else if(connection.getBody().getType().equalsIgnoreCase("binary")) {
                            var dataString = data.split(",");
                            var contentType = getContentType(dataString[0]);

                            formRequest.setData(null);
                            formRequest.setDataBinary(Base64.decodeBase64(dataString[1]));
                            formRequest.setContentType(contentType);
                        }
                    }
                }
            });
        }
    }

    private String getContentType(String s) {
        var pattern = "^data:(.*?);base64";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(s);
        if(m.find()) {
            return m.group(1);
        }
        return null;
    }

    private void addToStatistics(Project project, Instant start, Instant stop) {
        var entry = new StatisticsEntry(Timestamp.from(Instant.now()), Duration.between(start, stop).toMillis());
        statisticsRepository.addEntry(project.getId(), entry);
    }

    private String parseUrl(URL url, Project project) {
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
