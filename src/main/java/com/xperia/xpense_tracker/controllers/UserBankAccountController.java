package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.entities.tracker.UserBankAccount;
import com.xperia.xpense_tracker.models.request.BankAccountRequest;
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
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<AbstractResponse> upsertBankAccount(@AuthenticationPrincipal UserDetails userDetails,
                                                              @RequestBody BankAccountRequest bankAccountRequest){
        TrackerUser user = (TrackerUser) userDetails;
        try{
            bankAccountService.upsertBankAccount(user, bankAccountRequest);
            LOGGER.info("Updated bank account for the user : {}", user.getEmail());
            return ResponseEntity.ok(new SuccessResponse("Updated bank account"));
        }catch (Exception ex){
            LOGGER.error("Error saving bank account for the user : {}", user.getEmail(), ex);
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error saving bank account"));
        }
    }

    @GetMapping("/types")
    public ResponseEntity<AbstractResponse> getBankAccountTypes(){
        try{
            return ResponseEntity.ok(new SuccessResponse(bankAccountService.fetchBankAccountTypes()));
        }catch (Exception ex){
            LOGGER.error("Error fetching bank account types : ", ex);
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error fetching bank account types"));
        }
    }

    @DeleteMapping
    public ResponseEntity<AbstractResponse> deleteBankAccount(@RequestParam("id") String bankAccountId,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        try{
            TrackerUser user = (TrackerUser) userDetails;
            Optional<List<UserBankAccount>> userBankAccounts = bankAccountService.findBankAccountsOfUser(user);
            if (userBankAccounts.isEmpty()){
                LOGGER.error("Unable to find any bank accounts for the user : {}", user.getId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Bank account doesnt belong to user"));
            }
            Optional<UserBankAccount> selectedAccount = userBankAccounts.get()
                    .stream()
                    .filter(bankAccount -> bankAccount.getId().equalsIgnoreCase(bankAccountId))
                    .findFirst();
            if (selectedAccount.isEmpty()){
                LOGGER.error("Couldn't find bank account provided in request {} for user  : {}", bankAccountId, user.getId());
                return ResponseEntity.badRequest().body(new ErrorResponse("Couldn't find bank account with the provided id"));
            }
            bankAccountService.removeBankAccount(user, selectedAccount.get());
            return ResponseEntity.ok(new SuccessResponse("Removed bank account"));
        }catch (Exception ex){
            LOGGER.error("Error deleting bank account : {} for user : {}", bankAccountId, "" , ex);
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error deleting bank account"));
        }
    }
}
