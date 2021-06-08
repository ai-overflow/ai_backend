package de.hskl.ki.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.hskl.ki.models.yaml.ConfigDLYaml;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class SimpleDLYamlReader {
    public static final String CONFIG_DL_YAML = "config.dl.yaml";
    public ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public SimpleDLYamlReader() {
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public ConfigDLYaml read(Path path) throws IOException {
        var configDlPath = path.resolve(SimpleDLYamlReader.CONFIG_DL_YAML);

        return mapper.readValue(configDlPath.toFile(), ConfigDLYaml.class);
    }
}
