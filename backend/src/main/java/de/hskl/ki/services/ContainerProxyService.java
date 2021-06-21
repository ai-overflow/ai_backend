package de.hskl.ki.services;

import de.hskl.ki.config.properties.DockerManagerProperties;
import de.hskl.ki.config.properties.ProjectProperties;
import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.container.ContainerResponse;
import de.hskl.ki.services.processor.SimpleFileProcessor;
import de.hskl.ki.services.processor.SimpleJsonProcessor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContainerProxyService {
    private static final String CONTAINER_PROXY_PATH = "/api/v1/";
    private final SimpleFileProcessor<ContainerResponse[]> fileProcessor = new SimpleJsonProcessor<>(ContainerResponse[].class);
    @Autowired
    private DockerManagerProperties dockerManagerProperties;
    @Autowired
    private ProjectProperties projectProperties;
    @Autowired
    private ProjectRepository projectRepository;

    public Optional<String> startContainer(String containerId) throws IOException {
        return containerRequest(containerId, "POST");
    }

    public Optional<String> stopContainer(String containerId) throws IOException {
        return containerRequest(containerId, "DELETE");
    }

    public List<String> getAllContainer() throws IOException {
        Optional<ContainerResponse[]> containerResponse = fileProcessor.read(requestContainerURL("GET"));
        if (containerResponse.isPresent()) {
            var containers = containerResponse.get();
            return Arrays.stream(containers)
                    .filter(e -> e.getName().startsWith(projectProperties.getProjectContainerPrefix()))
                    .map(ContainerResponse::getName)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private Optional<String> containerRequest(String id, String method) throws IOException {
        Optional<Project> projectWrapper = projectRepository.findById(id);
        if (projectWrapper.isPresent()) {
            Project project = projectWrapper.get();
            Path path = Path.of(project.getProjectPath()).getFileName();
            String requestString = "{\"project_folder\": \"" + path.toString() + "\"}";

            String responseStream = requestContainerURL(method, requestString);
            return Optional.of(responseStream);
        }

        return Optional.empty();
    }

    private String requestContainerURL(String method) throws IOException {
        return requestContainerURL(method, "");
    }

    private String requestContainerURL(String method, String requestString) throws IOException {
        URL url = new URL("http://" +
                dockerManagerProperties.getContainerHost() +
                ":" + dockerManagerProperties.getContainerPort() +
                CONTAINER_PROXY_PATH + "container");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setDoOutput(true);
        if (!requestString.isEmpty()) {
            try (OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream())) {
                os.write(requestString);
            }
        }
        con.connect();
        return IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8);
    }
}
