package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.exception.customexception.TrackerException;
import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.models.response.FileUploadResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.services.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/file")
public class FileController {

    @Autowired
    private UploadService uploadService;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @PostMapping("/upload/statement")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbstractResponse> uploadStatement(@RequestParam("file") MultipartFile file,
                                                            @AuthenticationPrincipal UserDetails userDetails){
      try{
          String fileName = uploadService.uploadFile(file, userDetails);
          return ResponseEntity.ok().body(new SuccessResponse(new FileUploadResponse("File uploaded", fileName)));
      }catch (IOException | IllegalArgumentException ex){
          return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
      }catch (Exception ex){
          return ResponseEntity.internalServerError().body(new ErrorResponse(ex.getMessage()));
      }
    }

    @PostMapping("/upload/attachment")
    public ResponseEntity<AbstractResponse> uploadAttachment(@RequestParam("file") MultipartFile file,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        try{
            String fileName = uploadService.uploadAttachment(file, userDetails);
            return ResponseEntity.ok().body(new SuccessResponse(new FileUploadResponse("File uploaded", fileName)));
        }catch (IOException | IllegalArgumentException ex){
            LOGGER.error("Error occurred while upload file : {}", ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }catch (Exception ex){
            LOGGER.error("Exception occurred while upload file : {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().body(new ErrorResponse(ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> fetchAttachment(@RequestParam("id") String fileName){
        try{
            Resource foundResource = uploadService.fetchAttachment(fileName);
            return ResponseEntity.ok().body(foundResource);
        }catch (TrackerException ex){
            return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorResponse(ex.getMessage()));
        } catch (Exception ex){
            LOGGER.error("Exception while fetching file : {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().body(new ErrorResponse(ex.getMessage()));
        }
    }
}
