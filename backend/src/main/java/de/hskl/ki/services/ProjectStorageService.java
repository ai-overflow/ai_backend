package de.hskl.ki.services;

import de.hskl.ki.config.properties.ProjectProperties;
import de.hskl.ki.config.properties.SpringProperties;
import de.hskl.ki.models.exceptions.AIException;
import de.hskl.ki.services.interfaces.StorageService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ProjectStorageService implements StorageService {
    private final ProjectProperties projectProperties;
    private final Path projectFolder;

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
            throw new AIException("Unable to create project directory(" + pathInfo + ") for project", ProjectStorageService.class);
        }
    }

    @Override
    public Path generateStorageFolder() {
        if (this.projectFolder == null) {
            throw new AIException("projectFolder may not be null", ProjectStorageService.class);
        }

        Path tempDirWithPrefix;
        try {
            tempDirWithPrefix = Files.createTempDirectory(this.projectFolder, projectProperties.getProjectContainerPrefix());
        } catch (IOException e) {
            throw new AIException("Unable to create temporary File: " + e.getMessage(), ProjectStorageService.class);
        }
        return tempDirWithPrefix;
    }
}
