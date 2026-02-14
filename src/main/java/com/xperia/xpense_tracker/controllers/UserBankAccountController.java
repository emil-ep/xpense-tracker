package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.entities.tracker.UserBankAccount;
import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.services.UserBankAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/userBankAccount")
public class UserBankAccountController {

    @Autowired
    private UserBankAccountService bankAccountService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBankAccountController.class);

    @GetMapping
    public ResponseEntity<AbstractResponse> getUserBankAccounts(@AuthenticationPrincipal UserDetails userDetails){
        try{
            TrackerUser user = (TrackerUser) userDetails;
            Optional<List<UserBankAccount>> userBankAccounts = bankAccountService.findBankAccountsOfUser(user);
            if (userBankAccounts.isEmpty()){
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Couldn't find any bank account associated to the user"));
            }
            return ResponseEntity.ok(new SuccessResponse(userBankAccounts.get()));
        }catch (Exception ex){
            LOGGER.error("Error fetching bank accounts : {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error fetching user bank accounts"));
        }
    }
}
