package de.hskl.ki.services.processor;

import de.hskl.ki.models.yaml.dlconfig.ConfigDLYaml;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class SimpleYamlProcessorTest {

    @Test
    void testFindRootFolder(@TempDir Path tempDir) throws IOException {
        var subfolderName = "testDir/subDir/subDir2";
        var fileName = "config.dl.yaml";
        var subfolder = tempDir.resolve(subfolderName);

        if (!subfolder.toFile().mkdirs())
            fail("unable to create temp dir");
        if (!subfolder.resolve(fileName).toFile().createNewFile())
            fail("failed to create config file");

        for (int i = 0; i < 10; i++) {
            var tempPath = tempDir.resolve(String.valueOf(i));
            tempPath.toFile().mkdirs();
            tempPath.resolve(i + ".yaml").toFile().createNewFile();
        }

        var dlConfigYamlReader = new SimpleYamlProcessor<>(ConfigDLYaml.class);
        var folder = dlConfigYamlReader.findDlConfigFolder(tempDir);
        if (folder.size() != 1)
            fail("Multiple folders found");

        assertEquals(tempDir.resolve(subfolderName).resolve(fileName), folder.get(0));
    }
}
