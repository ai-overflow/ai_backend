package de.hskl.ki.services;

import de.hskl.ki.config.properties.ProjectProperties;
import de.hskl.ki.config.properties.SpringProperties;
import de.hskl.ki.services.interfaces.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ProjectStorageService implements StorageService {
    public static final String PROJECT_PREFIX = "project_";
    private final Logger logger = LoggerFactory.getLogger(ProjectStorageService.class);
    private final ProjectProperties projectProperties;
    private Path projectFolder;

    @Inject
    public ProjectStorageService(ProjectProperties projectProperties, SpringProperties springProperties) {
        this.projectProperties = projectProperties;

        String pathInfo = springProperties.hasEnvironment("dev") ?
                projectProperties.getHostDir() :
                projectProperties.getDirectory();

        this.projectFolder = Path.of(pathInfo);

        try {
            Files.createDirectories(this.projectFolder);
        } catch (IOException e) {
            logger.error("Unable to create project directory(" + pathInfo + ") for project");
            this.projectFolder = null;
        }
    }

    @Override
    public Optional<Path> generateStorageFolder() {
        if (this.projectFolder == null) return Optional.empty();

        Path tempDirWithPrefix;
        try {
            tempDirWithPrefix = Files.createTempDirectory(this.projectFolder, projectProperties.getProjectContainerPrefix());
        } catch (IOException e) {
            logger.error("Unable to create temporary File: " + e.toString());
            return Optional.empty();
        }
        return Optional.of(tempDirWithPrefix);
    }
}
