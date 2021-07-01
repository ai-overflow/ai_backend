package de.hskl.ki.services.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleYamlProcessor<T> extends SimpleFileProcessor<T> {
    private static final String CONFIG_DL_YAML = "config.dl";

    public SimpleYamlProcessor(Class<T> classType) {
        super(classType);
        mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public Optional<T> readDlConfig(Path path) throws IOException {
        return read(path, CONFIG_DL_YAML, List.of("yaml", "yml"));
    }

    public List<Path> findDlConfigFolder(Path rootPath) throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:.*" + Pattern.quote(CONFIG_DL_YAML) + "\\.(?:yaml|yml)");

        try (Stream<Path> files = Files.walk(rootPath)) {
            return files
                    .filter(matcher::matches)
                    .collect(Collectors.toList());
        }
    }
}
