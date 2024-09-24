package com.xperia.xpense_tracker.controllers;
import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/v1/expense")
public class ExpenseController {

    @Value("${file.upload.path}")
    private String fileUploadPath;

    @Autowired
    private ExpenseService expenseService;

    @PutMapping("/save")
    public ResponseEntity<AbstractResponse> processExpense(@RequestParam("fileName") String fileName){

        try{
            Path uploadedPath = Paths.get(fileUploadPath);
            Path filePath = uploadedPath.resolve(fileName);
            File file = filePath.toFile();
            if (!file.exists()){
                throw new IOException("File not found");
            }
            //TODO complete the implementation
            expenseService.processExpenseFromFile(file);
        }catch (IOException ex){
            return ResponseEntity.badRequest().body(new ErrorResponse("Error while processing file"));
        }
        return null;
    }
}
