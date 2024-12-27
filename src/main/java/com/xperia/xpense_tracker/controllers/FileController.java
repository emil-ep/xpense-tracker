package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.models.response.FileUploadResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.services.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/file")
public class FileController {

    @Autowired
    private UploadService uploadService;

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
}
