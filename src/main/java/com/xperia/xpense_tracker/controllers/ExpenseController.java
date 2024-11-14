package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.models.ExpenseAggregateType;
import com.xperia.xpense_tracker.models.entities.ExpenseFields;
import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.request.StatementPreviewRequest;
import com.xperia.xpense_tracker.models.response.*;
import com.xperia.xpense_tracker.services.ExpenseService;
import com.xperia.xpense_tracker.services.StatementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseController.class);

    @GetMapping
    public ResponseEntity<AbstractResponse> getExpenses(@RequestParam(value = "page", defaultValue = "1") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                                        @AuthenticationPrincipal UserDetails userDetails){
        try{
            Page<Expenses> expenses = expenseService.getExpenses(userDetails,
                    PageRequest.of(
                            page - 1,
                            size,
                            Sort.by(Sort.Direction.DESC, "transactionDate")));
            return ResponseEntity.ok(new SuccessResponse(
                    new ExpensePaginatedResponse(
                            expenses.getTotalPages(),
                            expenses.getTotalElements(),
                            expenses.getSize(),
                            expenses.getNumber(),
                            expenses.getContent()
                    )));
        }catch (TrackerBadRequestException ex){
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }catch (Exception ex) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Unable to process this request now!"));
        }
    }

    @PutMapping("/save")
    public ResponseEntity<AbstractResponse> processExpense(@RequestParam("fileName") String fileName,
                                                           @RequestBody StatementPreviewRequest request,
                                                           @AuthenticationPrincipal UserDetails userDetails){

        try{
            LOGGER.debug("received request for saving expenses fileName: {}, user: {}", fileName, userDetails.getUsername());
            Path uploadedPath = Paths.get(fileUploadPath);
            Path filePath = uploadedPath.resolve(fileName);
            File file = filePath.toFile();
            if (!file.exists()){
                throw new IOException("File not found");
            }
            expenseService.processExpenseFromFile(file, request, userDetails, false);
            LOGGER.info("Saved expense fileName: {}, user: {}", fileName, userDetails.getUsername());
            return ResponseEntity.ok(new SuccessResponse("Saved expense"));
        } catch (Exception ex){
            LOGGER.debug("Exception while saving expense fileName: {}, user: {}, ex: {}", fileName, userDetails.getUsername(), ex.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse("Error while processing file"));
        }
    }

    @GetMapping("/statement/mapper")
    public ResponseEntity<AbstractResponse> viewStatementMapper(@RequestParam("fileName") String fileName,
                                                                @AuthenticationPrincipal UserDetails userDetails){
        LOGGER.debug("received request for statement mapper : {}", fileName);
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

    @PostMapping("/statement/preview")
    public ResponseEntity<AbstractResponse> viewStatementPreview(@RequestParam("fileName") String fileName,
                                                                 @AuthenticationPrincipal UserDetails userDetails,
                                                                 @RequestBody StatementPreviewRequest request){
        try{
            Path uploadedPath = Paths.get(fileUploadPath);
            Path filePath = uploadedPath.resolve(fileName);
            File file = filePath.toFile();
            if (!file.exists()){
                throw new IOException("File not found");
            }
            List<Expenses> expenses = expenseService.processExpenseFromFile(file, request, userDetails, true);
            return ResponseEntity.ok(new SuccessResponse(expenses));
        }catch (IOException ex){
            return ResponseEntity.badRequest().body(new ErrorResponse("Error while processing file"));
        }
    }

    @GetMapping("/aggregate")
    public ResponseEntity<AbstractResponse> fetchAggregatedExpenses(@RequestParam("by") String aggregatePeriod,
                                                                    @AuthenticationPrincipal UserDetails userDetails) {

        try{
            if(ExpenseAggregateType.findByType(aggregatePeriod) == null){
                throw new TrackerBadRequestException("by field provided incompatible values");
            }
            List<Object[]> response = expenseService.aggregateExpenses(aggregatePeriod, userDetails);
            return  ResponseEntity.ok(new SuccessResponse(response));
        }catch (Exception ex){
            LOGGER.error("Unable to fetch expenses based on aggregation : {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error fetching aggregated expenses"));
        }
    }
 }
