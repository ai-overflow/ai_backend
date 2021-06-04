package de.hskl.ki.services;

import de.hskl.ki.services.interfaces.StorageService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ProjectStorageService implements StorageService {
    private Path projectFolder;

    public ProjectStorageService() {
        String pathInfo = System.getenv("PROJECT_DIR");
        if (pathInfo != null && !pathInfo.isEmpty()) {
            this.projectFolder = Paths.get(System.getenv("PROJECT_DIR")).resolve("projects");
        } else {
            this.projectFolder = Path.of(System.getProperty("user.dir")).resolve("projects");
        }

        try {
            Files.createDirectories(this.projectFolder);
        } catch (IOException e) {
            // TODO: logging
            this.projectFolder = null;
        }
    }

    @Override
    public Optional<Path> generateStorageFolder() {
        if (this.projectFolder == null) return Optional.empty();

        Path tempDirWithPrefix;
        try {
            tempDirWithPrefix = Files.createTempDirectory(this.projectFolder, "project_");
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.of(tempDirWithPrefix);
    }
}
