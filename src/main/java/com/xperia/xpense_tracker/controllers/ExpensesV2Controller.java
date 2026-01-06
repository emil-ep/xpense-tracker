package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.models.response.SyncExpenseResponse;
import com.xperia.xpense_tracker.services.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v2/expenses")
public class ExpensesV2Controller {

    @Autowired
    private ExpenseService expenseService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpensesV2Controller.class);

    @PostMapping("/sync")
    public ResponseEntity<AbstractResponse> syncExpenses(@AuthenticationPrincipal UserDetails userDetails){

        try{
            String requestId = UUID.randomUUID().toString();
            expenseService.syncExpensesV2(userDetails, requestId);
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
}
