package de.hskl.ki.config.properties;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;

@Component
public class SpringProperties {
    private final Environment environment;

    @Inject
    public SpringProperties(Environment env) {
        this.environment = env;
    }

    public boolean hasEnvironment(String name) {
        return Arrays.asList(environment.getActiveProfiles()).contains(name);
    }
}
