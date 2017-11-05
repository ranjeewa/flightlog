package com.ranjeewa.flightlog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@Description("A service for saving and retrieving flight logs from the filesystem")
public class FileService {

    private final static Logger logger = LoggerFactory.getLogger(FileService.class);

    private final ServiceProperties serviceProperties;

    FileService(ServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    public String saveFile(InputStream inputStream) throws IOException {

        String fileName = UUID.randomUUID().toString();

        File target = new File(serviceProperties.getFilePath() + File.separator + fileName);

        try {
            java.nio.file.Files.copy(
                    inputStream,
                    target.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            if (target.length() == 0) {
                logger.debug("Received POST with empty body");
                target.delete();
                return null;
            } else {
                logger.info("Saved new flight log with name {}", fileName);
                return fileName;
            }

        } catch (IOException e) {
            logger.error("Error saving a new flight log, message is {}", e.getMessage());
            throw e;
        }
    }

    public File findFile(String fileName) {

        File source = new File(serviceProperties.getFilePath() + File.separator + fileName);
        return source.exists()? source : null;

    }
}
