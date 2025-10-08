package com.xperia.xpense_tracker.converter;

import org.springframework.web.multipart.MultipartFile;
import org.xperia.exception.TrackerUnknownException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultImageProcessor extends AbstractImageProcessor<MultipartFile, MultipartFile>{
    @Override
    MultipartFile convertImage(MultipartFile image, String path, String fileName) {
        //No conversion required
        return image;
    }

    @Override
    public void saveImage(MultipartFile imageToSave, String path, String fileName) {
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
            imageToSave.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new TrackerUnknownException("Error while transferring file to directory : " + path);
        }
    }
}
