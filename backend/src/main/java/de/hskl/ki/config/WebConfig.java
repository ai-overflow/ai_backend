package de.hskl.ki.config;

import de.hskl.ki.config.properties.DockerManagerProperties;
import de.hskl.ki.config.properties.InferenceProperties;
import de.hskl.ki.config.properties.ProjectProperties;
import de.hskl.ki.config.properties.ServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@EnableConfigurationProperties({ProjectProperties.class, DockerManagerProperties.class, InferenceProperties.class, ServerProperties.class})
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    ServerProperties serverProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(serverProperties.getUrl())
                .allowedMethods("GET", "PUT", "POST", "UPDATE", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}

