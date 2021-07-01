package de.hskl.ki.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

//@SpringBootTest
class UtilityTest {

    @Test
    void testRandomString() {
        String pattern = "[A-Za-z0-9]{20}";
        for (int i = 0; i < 20; i++) {
            assertTrue(Utility.generateRandomString(20).matches(pattern));
        }
    }

    @Test
    void testRandomStringLength() {
        for (int i = 0; i < 20; i++) {
            assertEquals(20, Utility.generateRandomString(20).length());
        }
    }

    @Test
    void testReplaceRootWithSubfolder(@TempDir Path tempDir) throws IOException {
        var subfolderName = "testDir/subDir/subDir2";
        var fileName = "config.dl.yaml";
        var subfolder = tempDir.resolve(subfolderName);

        if(!subfolder.toFile().mkdirs())
            fail("unable to create temp dir");
        if(!subfolder.resolve(fileName).toFile().createNewFile())
            fail("failed to create config file");

        Utility.replaceRootWithSubfolder(tempDir, subfolder);
        assertTrue(tempDir.resolve(fileName).toFile().exists());
    }

    @Test
    void testReplaceRootWithSubfolderSameFolder(@TempDir Path tempDir) throws IOException {
        var fileName = "config.dl.yaml";

        if(!tempDir.resolve(fileName).toFile().createNewFile())
            fail("failed to create config file");

        Utility.replaceRootWithSubfolder(tempDir, tempDir);
        assertTrue(tempDir.resolve(fileName).toFile().exists());
    }

    @Test
    void testReplaceRootWithSubfolderDifferentFolder(@TempDir Path tempDir, @TempDir Path other) throws IOException {
        var fileName = "config.dl.yaml";

        if(!other.resolve(fileName).toFile().createNewFile())
            fail("failed to create config file");

        Utility.replaceRootWithSubfolder(tempDir, other);
        assertTrue(tempDir.resolve(fileName).toFile().exists());
    }
}
