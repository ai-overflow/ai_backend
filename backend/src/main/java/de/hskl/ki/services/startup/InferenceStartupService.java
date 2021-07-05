package de.hskl.ki.services.startup;

import de.hskl.ki.config.properties.DbProperties;
import de.hskl.ki.config.properties.SpringProperties;
import de.hskl.ki.models.exceptions.AIException;
import de.hskl.ki.services.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class InferenceStartupService {

    private final Logger logger = LoggerFactory.getLogger(InferenceStartupService.class);

    @Autowired
    ProjectService projectService;

    @Autowired
    SpringProperties springProperties;

    @Autowired
    DbProperties dbProperties;

    @PostConstruct
    public void init() {
        System.out.println(dbProperties.getPassword());
        try {
            if (!springProperties.hasEnvironment("dev")) {
                logger.info("Starting up Inference Service");
                projectService.activateAllModels();
            }
        } catch (AIException e) {
            logger.error("There was an error during Triton startup... triton won't be enabled: {}", e.getMessage());
        }
    }
}
