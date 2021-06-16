package de.hskl.ki.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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

    public Optional<T> read(Path path) throws IOException {
        return read(path, CONFIG_DL_YAML, List.of("yaml", "yml"));
    }

    public Optional<T> read(Path path, String fileName, List<String> extentions) throws IOException {
        Path configDlPath = null;
        for(String extention : extentions) {
            var tmpName = path.resolve(fileName + "." + extention);
            if(tmpName.toFile().exists()) {
                configDlPath = tmpName;
            }
        }

        if(configDlPath != null)
            return Optional.of(mapper.readValue(configDlPath.toFile(), classType));
        return Optional.empty();
    }
}
