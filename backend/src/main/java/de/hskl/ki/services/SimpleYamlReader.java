package de.hskl.ki.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class SimpleYamlReader<T> {
    private static final String CONFIG_DL_YAML = "config.dl";
    private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final Class<T> classType;

    public SimpleYamlReader(Class<T> classType) {
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.classType = classType;
    }

    public Optional<T> readDlConfig(Path path) throws IOException {
        return read(path, CONFIG_DL_YAML, List.of("yaml", "yml"));
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
