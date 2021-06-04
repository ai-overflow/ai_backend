package de.hskl.ki.services.interfaces;

import java.nio.file.Path;
import java.util.Optional;

public interface StorageService {
    Optional<Path> generateStorageFolder();
}
