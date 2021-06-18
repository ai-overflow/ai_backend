package de.hskl.ki.resource;

import de.hskl.ki.config.properties.ProjectProperties;
import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.ProjectRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestControllerAdvice
@RequestMapping("/api/v1/cp/")
public class ContainerProxyResource {

    public static final String CONTAINER_PROXY_URL = "http://localhost:8085/api/v1/";
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    ProjectProperties projectProperties;

    @GetMapping("container")
    public List<String> getAllContainer() throws IOException {
        URL url = new URL(CONTAINER_PROXY_URL + "container");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        return List.of(projectProperties.getHostDir());
    }

    @PostMapping("container/{id}")
    public ResponseEntity<?> changeContainerStatus(@PathVariable String id) throws IOException {


        Optional<Project> projectWrapper = projectRepository.findById(id);
        if(projectWrapper.isPresent()) {
            Project project = projectWrapper.get();
            Path path = Path.of(project.getProjectPath()).getFileName();

            URL url = new URL(CONTAINER_PROXY_URL + "container");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            try (OutputStreamWriter  os = new OutputStreamWriter(con.getOutputStream())) {
                os.write("{\"project_folder\": \"" + path.toString() + "\"}");
            }
            con.connect();
            InputStream responseStream = con.getInputStream();
            return ResponseEntity.ok(IOUtils.toString(responseStream, StandardCharsets.UTF_8));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
