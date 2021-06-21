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

/**
 * This service is used to send and receive requests to a managed container
 */
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

    /**
     * Starts all Container which belong to the given project.
     * This will be done with a request to the container manager
     *
     * @param projectId Container Project ID
     * @return Response stream from the container manager
     * @throws IOException if there was an error during the request
     */
    public Optional<String> startContainer(String projectId) throws IOException {
        return containerRequest(projectId, "POST");
    }

    /**
     * Stops all Container which belong to the given project.
     * This will be done with a request to the container manager
     *
     * @param projectId Container Project ID
     * @return Response stream from the container manager
     * @throws IOException if there was an error during the request
     */
    public Optional<String> stopContainer(String projectId) throws IOException {
        return containerRequest(projectId, "DELETE");
    }

    /**
     * Returns all docker-compose names of containers which have currently running container and are managed by the container manager
     * @return List of all project names from the docker-compose
     * @throws IOException if there was an error during the request
     */
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

    /**
     * Sends a request to the container manager
     * @param projectId id of the requested container project
     * @param method HTTP method (GET/POST/...)
     * @return the response stream if successfully
     * @throws IOException if there was an error during the request
     */
    private Optional<String> containerRequest(String projectId, String method) throws IOException {
        Optional<Project> projectWrapper = projectRepository.findById(projectId);
        if (projectWrapper.isPresent()) {
            Project project = projectWrapper.get();
            Path path = Path.of(project.getProjectPath()).getFileName();
            String requestString = "{\"project_folder\": \"" + path.toString() + "\"}";

            String responseStream = requestContainerURL(method, requestString);
            return Optional.of(responseStream);
        }

        return Optional.empty();
    }

    /**
     * Helper function for HTTP request to the container manager
     * @param method HTTP method
     * @return request stream result
     * @throws IOException if there was an error during the request
     */
    private String requestContainerURL(String method) throws IOException {
        return requestContainerURL(method, "");
    }

    /**
     * Helper function for HTTP request to the container manager
     * @param method HTTP method
     * @param requestString HTTP body
     * @return request stream result
     * @throws IOException if there was an error during the request
     */
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
