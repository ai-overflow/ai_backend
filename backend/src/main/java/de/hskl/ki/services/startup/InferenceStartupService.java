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
import java.util.Timer;
import java.util.TimerTask;

@Component
public class InferenceStartupService {

    private static final int MAXIMUM_RETRIES = 10;
    private static final long RETRY_CONNECTION_PERIOD = 2000L;
    private final Logger logger = LoggerFactory.getLogger(InferenceStartupService.class);

    @Autowired
    ProjectService projectService;

    @Autowired
    SpringProperties springProperties;

    @PostConstruct
    public void init() {
        final int[] retries = {0};

        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    if (!springProperties.hasEnvironment("dev")) {
                        logger.info("Starting up Inference Service");
                        projectService.activateAllModels();

                        logger.info("Triton connection successful");
                        cancel();
                    }
                } catch (AIException e) {
                    logger.warn("Unable to reach Triton server! retrying... ({})", e.getMessage());
                } finally {
                    if(++retries[0] >= MAXIMUM_RETRIES) {
                        cancel();
                        logger.error("There was an error during Triton startup... triton won't be enabled");
                    }
                }
            }
        };

        Timer timer = new Timer("Triton Startup Task");
        timer.scheduleAtFixedRate(task, 0L, RETRY_CONNECTION_PERIOD);
    }
}
