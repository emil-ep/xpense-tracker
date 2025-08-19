package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.cache.CacheService;
import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerException;
import com.xperia.xpense_tracker.models.ExpenseAggregateType;
import com.xperia.xpense_tracker.models.entities.ExpenseFields;
import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.entities.SyncStatus;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.request.StatementPreviewRequest;
import com.xperia.xpense_tracker.models.request.UpdateExpenseRequest;
import com.xperia.xpense_tracker.models.response.*;
import com.xperia.xpense_tracker.services.ExpenseService;
import com.xperia.xpense_tracker.services.StatementService;
import com.xperia.xpense_tracker.services.SyncStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.xperia.xpense_tracker.cache.CacheNames.METRICS_CACHE_NAME;

@RestController
@RequestMapping("/v1/expenses")
public class ExpenseController {

    @Value("${file.upload.path}")
    private String fileUploadPath;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private StatementService statementService;

    @Autowired
    private SyncStatusService syncStatusService;

    @Autowired
    private CacheService cacheManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseController.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yy");

    @GetMapping
    public ResponseEntity<AbstractResponse> getExpenses(@RequestParam(value = "page", defaultValue = "1") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                                        @RequestParam(value = "from") String fromDate,
                                                        @RequestParam(value = "to") String toDate,
                                                        @AuthenticationPrincipal UserDetails userDetails){
        try{
            LocalDate startDate;
            LocalDate endDate;
            startDate = LocalDate.parse(fromDate, DATE_TIME_FORMATTER);
            endDate = LocalDate.parse(toDate, DATE_TIME_FORMATTER);

            Page<Expenses> expenses = expenseService.getExpenses(userDetails,startDate, endDate,
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
        }catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid date format! Use dd/MM/yy."));
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
            cacheManager.clearCache(METRICS_CACHE_NAME, ((TrackerUser) userDetails).getId());
            return ResponseEntity.ok(new SuccessResponse("Saved expense"));
        } catch (Exception ex){
            LOGGER.debug("Exception while saving expense fileName: {}, user: {}", fileName, userDetails.getUsername(), ex);
            return ResponseEntity.badRequest().body(new ErrorResponse("Error while processing file"));
        }
    }

    @PatchMapping
    public ResponseEntity<AbstractResponse> updateExpense(@RequestParam("expense") String expenseId,
                                                          @RequestBody UpdateExpenseRequest request,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        LOGGER.debug("received request for updating expense : {}", expenseId);
        try{
            Expenses updatedExpense = expenseService.updateExpense(expenseId, request, userDetails);
            LOGGER.info("Updated expense with id : {}", expenseId);
            return ResponseEntity.ok(new SuccessResponse(updatedExpense));
        }catch (Exception ex){
            LOGGER.error("Unable to update expense : {} , exception : {}", expenseId, ex.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse("Unable to update expense"));
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
            Map<String, String> possibleMatches = expenseService.matchHeaders(header);
            return ResponseEntity.ok(new SuccessResponse(new StatementHeaderMapResponse(header, entityMap, possibleMatches)));
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
            List<MonthlyDebitSummary> response = expenseService.aggregateExpenses(aggregatePeriod, userDetails);
            return  ResponseEntity.ok(new SuccessResponse(response));
        }catch (Exception ex){
            LOGGER.error("Unable to fetch expenses based on aggregation : {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error fetching aggregated expenses"));
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<AbstractResponse> syncExpenses(@AuthenticationPrincipal UserDetails userDetails){
        try{
            String requestId = UUID.randomUUID().toString();
            expenseService.syncExpenses(userDetails, requestId);
            TrackerUser user = (TrackerUser) userDetails;
            LOGGER.info("Sync operation initiated for user : {} - requestId : {}", user.getId(), requestId);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            new SuccessResponse(
                                    new SyncExpenseResponse(
                                            "Sync operation initiated for user - requestId : " + requestId,
                                            requestId)
                            ));
        }catch (Exception ex){
            LOGGER.error("unable to sync expenses : {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error syncing expenses"));
        }
    }

    @GetMapping("/sync/status/{requestId}")
    public ResponseEntity<AbstractResponse> syncStatus(@AuthenticationPrincipal UserDetails userDetails,
                                                       @PathVariable("requestId") String requestId){
        try{
            Optional<SyncStatus> status = syncStatusService.fetchStatus(requestId);
            if (status.isPresent()){
                return ResponseEntity.ok(new SuccessResponse(status));
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Status for the requestId not found"));
            }
        }catch (Exception ex){
            LOGGER.error("unable to fetch sync status : {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error fetching sync status"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@expenseSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<AbstractResponse> softDeleteExpense(@AuthenticationPrincipal UserDetails userDetails,
                                                              @PathVariable("id") String id){
        try{
            expenseService.softDeleteExpense(id);
            return ResponseEntity.ok(new SuccessResponse("Deleted expense"));
        }catch (TrackerException ex){
            LOGGER.error("Unable to soft delete expense : {}", id, ex);
            return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorResponse(ex.getMessage()));
        } catch (Exception ex){
            LOGGER.error("Unable to soft delete expense : {}", id, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error while deleting expense"));
        }
    }
 }
