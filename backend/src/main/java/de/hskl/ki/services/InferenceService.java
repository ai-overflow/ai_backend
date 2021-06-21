package de.hskl.ki.services;

import de.hskl.ki.config.properties.InferenceProperties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InferenceService {
    private final Logger logger = LoggerFactory.getLogger(InferenceService.class);
    @Autowired
    private InferenceProperties inferenceProperties;

    /**
     *
     * @param projectDir
     * @return
     */
    public Optional<List<String>> moveModelsToTriton(Path projectDir) {
        Path modelsFolder = projectDir.resolve("models");
        if (!modelsFolder.toFile().exists()) {
            logger.info("Failed to find models folder in project folder");
            return Optional.empty();
        }

        File[] models = modelsFolder.toFile().listFiles(File::isDirectory);
        if (models == null) {
            logger.info("Error while listing models in model folder");
            return Optional.empty();
        }

        var globalModelsFolder = Path.of(inferenceProperties.getModelDir());
        var modelNames = new ArrayList<String>();
        for (File model : models) {
            if(globalModelsFolder.resolve(model.getName()).toFile().exists()) {
                logger.info("Model already exists: " + model);
                continue;
            }
            try {
                FileUtils.moveDirectory(model, globalModelsFolder.resolve(model.getName()).toFile());
                modelNames.add(model.getName());
            } catch (IOException e) {
                logger.info("Failed to move model: " + model + "(" + e + ")");
            }
        }
        return Optional.of(modelNames);
    }

    public void deleteModelFromTritonFolder(List<String> models) {
        var globalModelsFolder = Path.of(inferenceProperties.getModelDir());

        models.forEach(model -> {
            try {
                logger.info("Deleting Model: " + globalModelsFolder.resolve(model).toFile());
                // Safety check to prevent us from deleting the whole models folder
                if(!globalModelsFolder.resolve(model).equals(globalModelsFolder)) {
                    FileUtils.deleteDirectory(globalModelsFolder.resolve(model).toFile());
                }
            } catch (IOException e) {
                logger.info("Failed to delete model: " + model + "(" + e + ")");
            }
        });
    }

    public void activateProject(List<String> models) {
        changeProjectState(models, "load");
    }

    public void deactivateProject(List<String> models) {
        changeProjectState(models, "unload");
    }

    private void changeProjectState(List<String> models, String requestState) {
        for(String modelName: models) {
            try {
                URL url = new URL("http://triton:8000/v2/repository/models/" + modelName + "/" + requestState);
                URLConnection con = url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST"); // PUT is another valid option
                http.setDoOutput(true);
                http.getOutputStream();
                String result = IOUtils.toString(http.getInputStream(), StandardCharsets.UTF_8);
                System.out.println(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}