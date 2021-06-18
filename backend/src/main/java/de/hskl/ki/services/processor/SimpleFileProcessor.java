package de.hskl.ki.services.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public abstract class SimpleFileProcessor<T> {
    protected ObjectMapper mapper;
    private final Class<T> classType;

    public SimpleFileProcessor(Class<T> classType) {
        this.classType = classType;
    }

    public Optional<T> read(String value) {
        try {
            return Optional.of(mapper.readValue(value, classType));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public Optional<T> read(File fileName) throws IOException {
        if(fileName.exists())
            return Optional.of(mapper.readValue(fileName, classType));
        return Optional.empty();
    }

    public Optional<T> read(Path path, String fileName, List<String> extensions) throws IOException {
        Optional<File> configDlPath = findLocation(path, fileName, extensions);

        if(configDlPath.isPresent())
            return read(configDlPath.get());
        return Optional.empty();
    }

    public Optional<File> findLocation(Path path, String fileName, List<String> extensions) {
        Path configDlPath = null;
        for(String extension : extensions) {
            var tmpName = path.resolve(fileName + "." + extension);
            if(tmpName.toFile().exists()) {
                configDlPath = tmpName;
            }
        }

        return configDlPath != null ? Optional.of(configDlPath.toFile()) : Optional.empty();
    }

    public void write(File path, T value) throws IOException {
        mapper.writeValue(path, value);
    }
}
