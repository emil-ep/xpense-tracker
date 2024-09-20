package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.services.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileUploadServiceImpl implements UploadService {
    @Override
    public void uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();

        if(fileName == null || fileType == null){
            throw new IllegalArgumentException("Invalid file uploaded");
        }

        System.out.println("Processing file");
    }
}
