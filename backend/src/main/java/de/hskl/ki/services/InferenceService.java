package de.hskl.ki.services;

import de.hskl.ki.config.properties.InferenceProperties;
import de.hskl.ki.models.exceptions.AIException;
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

    public Optional<List<String>> moveModelsToTriton(Path projectDir) {
        return moveModelsToTriton(projectDir, null);
    }

    /**
     * @param projectDir
     * @param prefix
     * @return
     */
    public Optional<List<String>> moveModelsToTriton(Path projectDir, String prefix) {
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
            var modelProjectName = prefix != null && !prefix.isEmpty() ?
                    prefix + "_" + model.getName() :
                    model.getName();
            var modelFolderName = globalModelsFolder.resolve(modelProjectName).toFile();

            if (modelFolderName.exists()) {
                deleteModelFromTritonFolder(modelNames);
                logger.info("Model already exists: {}", model);
                throw new AIException("Model already exists: " + model, InferenceService.class);
            }
            try {
                FileUtils.copyDirectory(model, modelFolderName);
                modelNames.add(modelProjectName);
            } catch (IOException e) {
                deleteModelFromTritonFolder(modelNames);
                throw new AIException("Failed to move model: " + model + " (" + e.getMessage() + ")", InferenceService.class);
            }
        }
        return Optional.of(modelNames);
    }

    public void deleteModelFromTritonFolder(List<String> models) {
        var globalModelsFolder = Path.of(inferenceProperties.getModelDir());

        models.forEach(model -> {
            try {
                logger.info("Deleting Model: {}", globalModelsFolder.resolve(model).toFile());
                // Safety check to prevent us from deleting the whole models folder
                if (!globalModelsFolder.resolve(model).equals(globalModelsFolder)) {
                    FileUtils.deleteDirectory(globalModelsFolder.resolve(model).toFile());
                }
            } catch (IOException e) {
                throw new AIException("Failed to delete model: " + model + "(" + e.getMessage() + ")", InferenceService.class);
            }
        });
    }

    public void activateModel(String model) {
        changeProjectState(List.of(model), "load");
    }

    public void activateModel(List<String> models) {
        changeProjectState(models, "load");
    }

    public void deactivateModel(String model) {
        changeProjectState(List.of(model), "unload");
    }

    public void deactivateModel(List<String> models) {
        changeProjectState(models, "unload");
    }

    public String getStatus() {
        return requestTriton("v2/repository/index");
    }

    private void changeProjectState(List<String> models, String requestState) {
        for (String modelName : models) {
            var requestUrl = "v2/repository/models/" + modelName + "/" + requestState;
            requestTriton(requestUrl);
        }
    }

    private String requestTriton(String requestUrl) {
        var result = "";
        try {
            var url = new URL("http://triton:8000/" + requestUrl);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
            http.getOutputStream();
            result = IOUtils.toString(http.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AIException("Unable to connect to triton service: " + e.getMessage(), InferenceService.class);
        }
        return result;
    }
}
