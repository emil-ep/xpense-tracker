package com.xperia.xpense_tracker.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface UploadService {

    String uploadFile(MultipartFile file, UserDetails userDetails) throws IOException;
}
