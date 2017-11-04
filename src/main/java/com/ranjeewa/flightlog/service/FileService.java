package com.ranjeewa.flightlog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

@Component
public class FileService {

    private final static Logger logger = LoggerFactory.getLogger(FileService.class);

    private final ServiceProperties serviceProperties;

    FileService(ServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    public String saveFile(InputStream inputStream) throws IOException {

        //TODO replace with a UUID
        String fileName = Long.toString(System.currentTimeMillis());

        File target = new File(serviceProperties.getFilePath() + File.separator + fileName);

        try {
            java.nio.file.Files.copy(
                    inputStream,
                    target.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            logger.info("Saved new flight log with name {}", fileName);
            return fileName;
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
