package de.hskl.ki.services;

import de.hskl.ki.config.properties.ProjectProperties;
import de.hskl.ki.config.properties.SpringProperties;
import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.document.helper.ProjectAccessInfo;
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

/**
 * This service is used to manage Docker containers
 */
@Service
public class DockerService {

    private final Logger logger = LoggerFactory.getLogger(DockerService.class);
    private final SimpleFileProcessor<DockerComposeYaml> composeYamlReader = new SimpleYamlProcessor<>(DockerComposeYaml.class);
    @Autowired
    private ProjectProperties projectProperties;
    @Autowired
    private SpringProperties springProperties;

    /**
     * Manipulates a docker-compose file to fit the format of the container manager.
     * This will do the following:
     * <ul>
     *  <li>Remove Triton Service if it exists</li>
     *  <li>Add a custom network which is shared by all containers</li>
     *  <li>Change the docker-compose version to <strong>3</strong></li>
     *  <li>Change the path of all volumes to an absolute path on the host file system</li>
     *  <li>Sets a custom hostname for this service</li>
     *  <li>Replace all exposed ports to internal exposed ports</li>
     *  <li>Rename all services to a unique name</li>
     *  <li>Write all changes back to the database</li>
     * </ul>
     *
     * @param projectDir  Path to the project folder
     * @param projectInfo Additional Project information
     * @return success status
     * @throws IOException if there was an error during any write operation.
     *                     If this is triggered then there won't be any changes to the database
     */
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

            projectInfo.setAccessInfo(data.getAccessInfo());
            projectInfo.setServiceNames(data.getServices());
        } else {
            logger.warn("Failed to read compose config!");
            return false;
        }
        return true;
    }

    /**
     * This will make changes according to {@link #processComposeFile(Path, Project)}
     *
     * @param dockerComposeYaml Parsed docker compose
     * @param projectInfo       Additional Project information
     * @param projectDir        Path to the project folder
     * @return New Hostname and Service information
     */
    private HostnameServicePOD updateComposeFile(DockerComposeYaml dockerComposeYaml, Project projectInfo, Path projectDir) {
        removeTriton(dockerComposeYaml);
        addNetworkToContainer(dockerComposeYaml);
        changeComposeVersion(dockerComposeYaml);
        changeVolumePath(dockerComposeYaml, projectDir);

        Map<Integer, ProjectAccessInfo> accessInfo = replacePortWithExposeAndAddHostname(dockerComposeYaml);
        ArrayList<String> services = renameService(dockerComposeYaml, projectInfo);

        return new HostnameServicePOD(accessInfo, services);
    }

    /**
     * Change the path of all volumes to an absolute path on the host file system
     *
     * @param dockerComposeYaml Parsed docker compose
     * @param projectDir        Path to the project folder
     */
    private void changeVolumePath(DockerComposeYaml dockerComposeYaml, Path projectDir) {
        for (var service : dockerComposeYaml.getServices().values()) {
            service.setVolumes(service.getVolumes()
                    .stream()
                    .map(volume -> {
                        if (!(volume instanceof String)) return volume;

                        String volumeStr = (String) volume;
                        String[] volumes = volumeStr.split(":");
                        if (volumes.length < 2) return volume;
                        volumeStr = volumes[0];

                        try {
                            var pathName = new File(projectDir.toFile(), volumeStr).getCanonicalFile().toString();

                            if (!springProperties.hasEnvironment("dev")) {
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

    /**
     * Change the docker-compose version to <strong>3</strong>
     *
     * @param dockerComposeYaml Parsed docker compose
     */
    private void changeComposeVersion(DockerComposeYaml dockerComposeYaml) {
        //TODO: Maybe check if version is actually 3
        dockerComposeYaml.setVersion("3");
    }

    /**
     * Add a custom network which is shared by all containers
     *
     * @param dockerComposeYaml Parsed docker compose
     */
    private void addNetworkToContainer(DockerComposeYaml dockerComposeYaml) {
        String networkName = projectProperties.getInternalNetworkName();
        // Add Network to Container
        if (dockerComposeYaml.getNetworks() == null)
            dockerComposeYaml.setNetworks(new HashMap<>());
        dockerComposeYaml.getNetworks().put(networkName, new DockerNetwork(true));
        dockerComposeYaml.getServices().values().forEach(e -> {
            if (e.getNetworks() == null)
                e.setNetworks(new ArrayList<>());
            e.getNetworks().add(networkName);
        });
    }

    /**
     * Rename all services to a unique name
     *
     * @param dockerComposeYaml Parsed docker compose
     * @param projectInfo       Path to the project folder
     * @return List of new Service names
     */
    @NotNull
    private ArrayList<String> renameService(DockerComposeYaml dockerComposeYaml, Project projectInfo) {
        String containerPrefix = projectProperties.getProjectContainerPrefix();
        // Rename Service
        var services = new ArrayList<String>();
        var keySet = dockerComposeYaml
                .getServices()
                .keySet();
        for (String key : keySet) {
            String newName = containerPrefix +
                    projectInfo.getYaml().getName().replaceAll("[^A-Za-z0-9_]", "").toLowerCase() +
                    "_" +
                    key;
            services.add(newName);
            dockerComposeYaml.getServices().put(newName, dockerComposeYaml.getServices().remove(key)
            );
        }
        return services;
    }

    /**
     * Sets a custom hostname for this service
     * Replace all exposed ports to internal exposed ports
     *
     * @param dockerComposeYaml Parsed docker compose
     * @return Previous exposed port -> (Hostname, Internal exposed port)
     */
    private Map<Integer, ProjectAccessInfo> replacePortWithExposeAndAddHostname(DockerComposeYaml dockerComposeYaml) {
        Map<Integer, ProjectAccessInfo> portsMap = new HashMap<>();
        dockerComposeYaml.getServices().forEach((key, value) -> {
            var hostname = UUID.randomUUID();
            // Replace Ports with expose
            List<String> ports = value.getPorts();
            Map<Integer, ProjectAccessInfo> newPorts = ports
                    .stream()
                    .map(port -> {
                        String[] split = port.split(":");
                        if (split.length > 1) {
                            return Optional.of(Map.entry(Integer.valueOf(split[0]), new ProjectAccessInfo(hostname.toString(), Integer.valueOf(split[1]))));
                        }
                        return Optional.empty();
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(e -> (Map.Entry<Integer, ProjectAccessInfo>) e)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            value.setExpose(
                    newPorts.values()
                            .stream()
                            .map(e -> String.valueOf(e.getPort()))
                            .collect(Collectors.toList())
            );
            value.setHostname(hostname.toString());
            value.setPorts(null);

            portsMap.putAll(newPorts);
        });
        return portsMap;
    }

    /**
     * Remove Triton Service if it exists
     *
     * @param dockerComposeYaml Parsed docker compose
     */
    private void removeTriton(DockerComposeYaml dockerComposeYaml) {
        dockerComposeYaml.getServices().entrySet().removeIf(e -> {
            if (e.getValue().getImage() != null)
                return e.getValue().getImage().contains("triton");
            return false;
        });
    }

    private static class HostnameServicePOD {
        private final Map<Integer, ProjectAccessInfo> accessInfo;
        private final List<String> services;

        public HostnameServicePOD(Map<Integer, ProjectAccessInfo> accessInfo, List<String> services) {
            this.accessInfo = accessInfo;
            this.services = services;
        }

        public Map<Integer, ProjectAccessInfo> getAccessInfo() {
            return accessInfo;
        }

        public List<String> getServices() {
            return services;
        }
    }
}
