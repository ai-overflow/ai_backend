package de.hskl.ki.services;

import de.hskl.ki.db.document.Project;
import de.hskl.ki.models.yaml.compose.DockerComposeYaml;
import de.hskl.ki.models.yaml.compose.DockerNetwork;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DockerService {

    private final Logger logger = LoggerFactory.getLogger(DockerService.class);
    public static final String DL_PROJECT_NETWORK = "dl_project_network";
    private final SimpleYamlReader<DockerComposeYaml> composeYamlReader = new SimpleYamlReader<>(DockerComposeYaml.class);

    private static class HostnameServicePOD {
        public String hostname;
        public List<String> services;

        public HostnameServicePOD(String hostname, List<String> services) {
            this.hostname = hostname;
            this.services = services;
        }
    }

    public boolean processComposeFile(Path projectDir, Project projectInfo) throws IOException {
        var location = composeYamlReader.findLocation(projectDir, "docker-compose", List.of("yaml", "yml"));
        if (location.isEmpty()) {
            logger.warn("Unable to find docker-compose file!");
            return false;
        }

        var composeConfig = composeYamlReader.read(location.get());
        if (composeConfig.isPresent()) {
            var data = updateComposeFile(composeConfig.get(), projectInfo);
            composeYamlReader.write(location.get(), composeConfig.get());

            projectInfo.setHostname(data.hostname);
            projectInfo.setServiceNames(data.services);
        } else {
            logger.warn("Failed to read compose config!");
            return false;
        }
        return true;
    }

    public HostnameServicePOD updateComposeFile(DockerComposeYaml dockerComposeYaml, Project projectInfo) {
        removeTriton(dockerComposeYaml);
        replacePortWithExpose(dockerComposeYaml);
        AddNetworkToContainer(dockerComposeYaml);
        
        ArrayList<String> services = renameService(dockerComposeYaml, projectInfo);
        UUID hostname = setHostname(dockerComposeYaml);

        return new HostnameServicePOD(hostname.toString(), services);
    }

    @NotNull
    private UUID setHostname(DockerComposeYaml dockerComposeYaml) {
        // Set Hostname
        UUID hostname = UUID.randomUUID();
        dockerComposeYaml.getServices().values().forEach(service -> service.setHostname(hostname.toString()));
        return hostname;
    }

    private void AddNetworkToContainer(DockerComposeYaml dockerComposeYaml) {
        // Add Network to Container
        if(dockerComposeYaml.getNetworks() == null)
            dockerComposeYaml.setNetworks(new HashMap<>());
        dockerComposeYaml.getNetworks().put(DL_PROJECT_NETWORK, new DockerNetwork(true));
        dockerComposeYaml.getServices().values().forEach(e -> {
            if(e.getNetworks() == null)
                e.setNetworks(new ArrayList<>());
            e.getNetworks().add(DL_PROJECT_NETWORK);
        });
    }

    @NotNull
    private ArrayList<String> renameService(DockerComposeYaml dockerComposeYaml, Project projectInfo) {
        // Rename Service
        var services = new ArrayList<String>();
        var keySet = dockerComposeYaml
                .getServices()
                .keySet();
        for (String key : keySet) {
            String newName = "project_" +
                    projectInfo.getYaml().getName().replaceAll("[^A-Za-z0-9_]", "").toLowerCase() +
                    "_" +
                    key;
            services.add(newName);
            dockerComposeYaml.getServices().put(newName, dockerComposeYaml.getServices().remove(key)
            );
        }
        return services;
    }

    private void replacePortWithExpose(DockerComposeYaml dockerComposeYaml) {
        dockerComposeYaml.getServices().forEach((key, value) -> {
            // Replace Ports with expose
            List<String> ports = value.getPorts();
            ports = ports
                    .stream()
                    .map(port -> Arrays.stream(port.split(":"))
                            .reduce((first, second) -> second)
                            .orElse("")
                    )
                    .collect(Collectors.toList());
            value.setExpose(ports);
            value.setPorts(null);
        });
    }

    private void removeTriton(DockerComposeYaml dockerComposeYaml) {
        dockerComposeYaml.getServices().entrySet().removeIf(e -> {
            if (e.getValue().getImage() != null)
                return e.getValue().getImage().contains("triton");
            return false;
        });
    }
}
