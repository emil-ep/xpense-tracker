package com.xperia.xpense_tracker.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.xperia.exception.TrackerUnknownException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultImageProcessor extends AbstractAttachmentProcessor<MultipartFile, MultipartFile> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultImageProcessor.class);

    @Override
    MultipartFile convertAttachment(MultipartFile attachment, String path, String fileName) {
        //No conversion required
        return attachment;
    }

    @Override
    public void saveAttachment(MultipartFile attachmentToSave, String path, String fileName) {
        Path uploadPath = Paths.get(path);
        if(!Files.exists(uploadPath)){
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new TrackerUnknownException("Error while creating directory : " + path);
            }
        }
        Path filePath = uploadPath.resolve(fileName);
        try {
            attachmentToSave.transferTo(filePath.toFile());
        } catch (IOException e) {
            LOGGER.error("Error while transferring file using DefaultImageProcessor : {}", e.getMessage(), e);
            throw new TrackerUnknownException("Error while transferring file to directory : " + path, e);
        }
    }
}
