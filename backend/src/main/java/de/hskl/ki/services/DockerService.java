package de.hskl.ki.services;

import de.hskl.ki.config.properties.ProjectProperties;
import de.hskl.ki.config.properties.SpringProperties;
import de.hskl.ki.db.document.Project;
import de.hskl.ki.models.yaml.compose.DockerComposeYaml;
import de.hskl.ki.models.yaml.compose.DockerNetwork;
import de.hskl.ki.services.processor.SimpleFileProcessor;
import de.hskl.ki.services.processor.SimpleYamlProcessor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DockerService {

    public static final String DL_PROJECT_NETWORK = "dl_project_network";
    private final Logger logger = LoggerFactory.getLogger(DockerService.class);
    private final SimpleFileProcessor<DockerComposeYaml> composeYamlReader = new SimpleYamlProcessor<>(DockerComposeYaml.class);
    @Autowired
    ProjectProperties projectProperties;
    @Autowired
    SpringProperties springProperties;

    public boolean processComposeFile(Path projectDir, Project projectInfo) throws IOException {
        var location = composeYamlReader.findLocation(projectDir, "docker-compose", List.of("yaml", "yml"));
        if (location.isEmpty()) {
            logger.warn("Unable to find docker-compose file!");
            return false;
        }

        var composeConfig = composeYamlReader.read(location.get());
        if (composeConfig.isPresent()) {
            var data = updateComposeFile(composeConfig.get(), projectInfo, projectDir);
            composeYamlReader.write(location.get(), composeConfig.get());

            projectInfo.setHostname(data.hostname);
            projectInfo.setServiceNames(data.services);
        } else {
            logger.warn("Failed to read compose config!");
            return false;
        }
        return true;
    }

    public HostnameServicePOD updateComposeFile(DockerComposeYaml dockerComposeYaml, Project projectInfo, Path projectDir) {
        removeTriton(dockerComposeYaml);
        replacePortWithExpose(dockerComposeYaml);
        addNetworkToContainer(dockerComposeYaml);
        changeComposeVersion(dockerComposeYaml);
        changeVolumePath(dockerComposeYaml, projectDir);

        ArrayList<String> services = renameService(dockerComposeYaml, projectInfo);
        UUID hostname = setHostname(dockerComposeYaml);

        return new HostnameServicePOD(hostname.toString(), services);
    }

    private void changeVolumePath(DockerComposeYaml dockerComposeYaml, Path projectDir) {
        for (var service : dockerComposeYaml.getServices().values()) {
            service.setVolumes(service.getVolumes()
                    .stream()
                    .map(volume -> {
                        if (!(volume instanceof String)) return volume;

                        String volumeStr = (String) volume;
                        String[] volumes = volumeStr.split(":");
                        if(volumes.length < 2) return volume;
                        volumeStr = volumes[0];

                        try {
                            String pathName = new File(projectDir.toFile(), volumeStr).getCanonicalFile().toString();

                            if(!springProperties.hasEnvironment("dev")) {
                                pathName = pathName.replaceAll("^" + projectProperties.getDirectory(), projectProperties.getHostDir());
                            }

                            pathName = pathName.replaceAll("^([A-Za-z]):", "$1").toLowerCase();
                            return "/host_mnt/" + pathName + ":" + volumes[1];
                        } catch (IOException e) {
                            return volume;
                        }
                    })
                    .collect(Collectors.toList()));
        }
    }

    private void changeComposeVersion(DockerComposeYaml dockerComposeYaml) {
        //TODO: Maybe check if version is actually 3
        dockerComposeYaml.setVersion("3");
    }

    @NotNull
    private UUID setHostname(DockerComposeYaml dockerComposeYaml) {
        // Set Hostname
        UUID hostname = UUID.randomUUID();
        dockerComposeYaml.getServices().values().forEach(service -> service.setHostname(hostname.toString()));
        return hostname;
    }

    private void addNetworkToContainer(DockerComposeYaml dockerComposeYaml) {
        // Add Network to Container
        if (dockerComposeYaml.getNetworks() == null)
            dockerComposeYaml.setNetworks(new HashMap<>());
        dockerComposeYaml.getNetworks().put(DL_PROJECT_NETWORK, new DockerNetwork(true));
        dockerComposeYaml.getServices().values().forEach(e -> {
            if (e.getNetworks() == null)
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

    private static class HostnameServicePOD {
        public String hostname;
        public List<String> services;

        public HostnameServicePOD(String hostname, List<String> services) {
            this.hostname = hostname;
            this.services = services;
        }
    }
}
