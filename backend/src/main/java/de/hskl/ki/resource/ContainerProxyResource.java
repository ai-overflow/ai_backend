package de.hskl.ki.resource;

import de.hskl.ki.config.properties.DockerManagerProperties;
import de.hskl.ki.config.properties.ProjectProperties;
import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.container.ContainerResponse;
import de.hskl.ki.services.ProjectStorageService;
import de.hskl.ki.services.processor.SimpleFileProcessor;
import de.hskl.ki.services.processor.SimpleJsonProcessor;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequestMapping("/api/v1/cp/")
public class ContainerProxyResource {

    private static final String CONTAINER_PROXY_PATH = "/api/v1/";
    @Autowired
    ProjectProperties projectProperties;
    @Autowired
    DockerManagerProperties dockerManagerProperties;
    SimpleFileProcessor<ContainerResponse[]> fileProcessor = new SimpleJsonProcessor<>(ContainerResponse[].class);
    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("container")
    public List<String> getAllContainer() throws IOException {
        Optional<ContainerResponse[]> containerResponse = fileProcessor.read(requestContainerURL("GET"));
        if (containerResponse.isPresent()) {
            var containers = containerResponse.get();
            return Arrays.stream(containers)
                    .filter(e -> e.getName().startsWith(ProjectStorageService.PROJECT_PREFIX))
                    .map(ContainerResponse::getName)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @PostMapping("container/{id}")
    public ResponseEntity<?> startContainer(@PathVariable String id) throws IOException {
        Optional<String> value = containerRequest(id, "POST");
        if (value.isPresent()) {
            return ResponseEntity.ok(value.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("container/{id}")
    public ResponseEntity<?> stopContainer(@PathVariable String id) throws IOException {
        Optional<String> value = containerRequest(id, "DELETE");
        if (value.isPresent()) {
            return ResponseEntity.ok(value.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @NotNull
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
        System.out.println(url);

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
