package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.exception.customexception.TrackerNotFoundException;
import com.xperia.xpense_tracker.exception.customexception.TrackerUnknownException;
import com.xperia.xpense_tracker.models.entities.Statements;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.services.StatementService;
import com.xperia.xpense_tracker.services.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;

@Service
public class FileUploadServiceImpl implements UploadService {

    @Value("${file.upload.path}")
    private String fileUploadPath;

    @Value("${file.upload.attachment.path}")
    private String attachmentUploadPath;

    @Autowired
    private StatementService statementService;

    private static final String[] ALLOWED_FILE_TYPES = {"csv", "xlsx", "DELIMITED", "pdf"};

    private static final String[] ALLOWED_ATTACHMENT_TYPE = {"jpeg", "jpg", "pdf", "png"};

    private static final Logger LOG = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    @Override
    public String uploadFile(MultipartFile file, UserDetails userDetails) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        TrackerUser user = (TrackerUser) userDetails;

        if(fileName == null || fileType == null){
            LOG.error("file name or file type is empty");
            throw new IllegalArgumentException("Invalid file uploaded");
        }

        String fileExtension = fileName.split("\\.")[1];

        if(Arrays.stream(ALLOWED_FILE_TYPES).noneMatch(allowedType -> allowedType.equals(fileExtension))){
            LOG.error("Invalid file type is received for upload : fileType = {}", fileType);
            throw new IllegalArgumentException("Invalid file uploaded");
        }
        String customFileName = user.getId() + "_" + Instant.now().getEpochSecond() + "_" + fileName;
        try{
            Path uploadPath = Paths.get(fileUploadPath);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(customFileName);
            file.transferTo(filePath.toFile());
            Statements statement = new Statements(customFileName, user, System.currentTimeMillis());
            statementService.saveStatement(statement);
            return customFileName;
        }catch (IOException ex){
            LOG.error("Error occurred while saving file : {}", ex.getMessage(), ex);
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public String uploadAttachment(MultipartFile file, UserDetails userDetails) throws IOException {

        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        TrackerUser user = (TrackerUser) userDetails;

        if(fileName == null || fileType == null){
            LOG.error("file name or file type is empty");
            throw new IllegalArgumentException("Invalid file uploaded");
        }

        String fileExtension = fileName.split("\\.")[1];
        if(Arrays.stream(ALLOWED_ATTACHMENT_TYPE).noneMatch(allowedType -> allowedType.equals(fileExtension))){
            LOG.error("Invalid file type is received for upload : fileType = {}", fileType);
            throw new IllegalArgumentException("Invalid file uploaded");
        }
        String customFileName = user.getId() + "_" + Instant.now().getEpochSecond() + "_" + fileName;
        try{
            Path uploadPath = Paths.get(attachmentUploadPath);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(customFileName);
            file.transferTo(filePath.toFile());
            return customFileName;
        }catch (IOException ex){
            LOG.error("Error occurred while saving file : {}", ex.getMessage(), ex);
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public Resource fetchAttachment(String id) {
       Path filePath = Paths.get(attachmentUploadPath).resolve(id).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()){
                throw new TrackerNotFoundException("Couldn't locate requested file");
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return resource;
        } catch (MalformedURLException e) {
            LOG.error("Exception while creating resource : {}", e.getMessage(), e);
            throw new TrackerUnknownException("Exception while fetching attachment");
        } catch (IOException e) {
            throw new TrackerUnknownException("Exception while probing content type");
        }
    }
}
