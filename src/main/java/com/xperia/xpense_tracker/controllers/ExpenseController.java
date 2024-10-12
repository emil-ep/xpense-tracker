package com.xperia.xpense_tracker.controllers;
import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.models.entities.ExpenseFields;
import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.models.response.StatementHeaderMapResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.services.ExpenseService;
import com.xperia.xpense_tracker.services.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1/expenses")
public class ExpenseController {

    @Value("${file.upload.path}")
    private String fileUploadPath;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private StatementService statementService;

    @GetMapping
    public ResponseEntity<AbstractResponse> getExpenses(@AuthenticationPrincipal UserDetails userDetails){
        try{
            List<Expenses> expenses = expenseService.getExpenses(userDetails);
            return ResponseEntity.ok(new SuccessResponse(expenses));
        }catch (TrackerBadRequestException ex){
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }catch (Exception ex) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Unable to process this request now!"));
        }
    }

    @PutMapping("/save")
    public ResponseEntity<AbstractResponse> processExpense(@RequestParam("fileName") String fileName,
                                                           @AuthenticationPrincipal UserDetails userDetails){

        try{
            Path uploadedPath = Paths.get(fileUploadPath);
            Path filePath = uploadedPath.resolve(fileName);
            File file = filePath.toFile();
            if (!file.exists()){
                throw new IOException("File not found");
            }
            expenseService.processExpenseFromFile(file, userDetails);
        }catch (IOException ex){
            return ResponseEntity.badRequest().body(new ErrorResponse("Error while processing file"));
        }
        return null;
    }

    @GetMapping("/statement/mapper")
    public ResponseEntity<AbstractResponse> viewStatementMapper(@RequestParam("fileName") String fileName,
                                                                @AuthenticationPrincipal UserDetails userDetails){
        try{
            Path uploadedPath = Paths.get(fileUploadPath);
            Path filePath = uploadedPath.resolve(fileName);
            File file = filePath.toFile();
            if (!file.exists()){
                throw new IOException("File not found");
            }
            List<String> header = statementService.extractHeaderMapper(file);
            List<String> entityMap = Arrays.asList(
                    ExpenseFields.TRANSACTION_DATE.getFieldName(),
                    ExpenseFields.DESCRIPTION.getFieldName(),
                    ExpenseFields.BANK_REF_NO.getFieldName(),
                    ExpenseFields.DEBIT.getFieldName(),
                    ExpenseFields.CREDIT.getFieldName(),
                    ExpenseFields.CLOSING_BALANCE.getFieldName()
                    );
            return ResponseEntity.ok(new SuccessResponse(new StatementHeaderMapResponse(header, entityMap)));
        }catch (IOException ex){
            return ResponseEntity.badRequest().body(new ErrorResponse("Error while processing file"));
        }
    }
}
