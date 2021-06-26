package de.hskl.ki.services.startup;

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

    @PostConstruct
    public void init() {
        logger.info("Starting up Inference Service");
        projectService.activateAllModels();
    }
}
