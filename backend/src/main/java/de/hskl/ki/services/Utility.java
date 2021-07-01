package de.hskl.ki.services;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Locale;

public class Utility {
    private static final SecureRandom secureRandom = new SecureRandom();

    private Utility() {
    }

    public static String generateRandomString(int length) {
        var upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var lower = upper.toLowerCase(Locale.ROOT);
        var digits = "0123456789";
        var alphanum = upper + lower + digits;
        char[] symbols = alphanum.toCharArray();
        char[] buffer = new char[length];

        for (int idx = 0; idx < buffer.length; ++idx)
            buffer[idx] = symbols[secureRandom.nextInt(symbols.length)];

        return new String(buffer);
    }

    public static void replaceRootWithSubfolder(Path root, Path subfolder) throws IOException {
        if (root.equals(subfolder)) return;

        var tempDir = Files.createTempDirectory(null).resolve("tmp").toFile();
        FileUtils.moveDirectory(subfolder.toFile(), tempDir);
        FileUtils.deleteDirectory(root.toFile());
        FileUtils.moveDirectory(tempDir, root.toFile());
    }
}
