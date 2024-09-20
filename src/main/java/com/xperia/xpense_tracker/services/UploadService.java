package com.xperia.xpense_tracker.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface UploadService {

    void uploadFile(MultipartFile file) throws IOException;
}
