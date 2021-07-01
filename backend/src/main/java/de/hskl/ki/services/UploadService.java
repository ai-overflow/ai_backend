package de.hskl.ki.services;

import de.hskl.ki.models.exceptions.AIException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class UploadService {
    private final Logger logger = LoggerFactory.getLogger(UploadService.class);
    @Autowired
    ProjectStorageService projectStorageService;

    public Path createProjectFromArchive(MultipartFile projectArchive) {
        String extension = FilenameUtils.getExtension(projectArchive.getOriginalFilename());
        if (extension == null || !extension.equals("zip")) {
            throw new AIException("File is not a ZIP Archive", UploadService.class);
        }

        var buffer = new byte[2048];
        var dir = projectStorageService.generateStorageFolder();

        try (ZipInputStream zip = new ZipInputStream(new BufferedInputStream(projectArchive.getInputStream()))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                logger.info("Entry: {} len {} added {}", entry.getName(), entry.getSize(), new Date(entry.getTime()));

                var outpath = dir.resolve(entry.getName()).toFile();
                if (entry.isDirectory()) {
                    outpath.mkdirs();
                } else {
                    try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outpath))) {
                        int len;
                        while ((len = zip.read(buffer)) > 0) {
                            output.write(buffer, 0, len);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new AIException("Unable to create a Project from a zip: " + e.getMessage(), UploadService.class);
        }

        return dir;
    }
}
