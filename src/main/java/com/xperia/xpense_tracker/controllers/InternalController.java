package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.services.InternalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xperia.exception.TrackerNotFoundException;
import org.xperia.models.SharedMailDetails;
import org.xperia.models.SharedUserSetting;
import org.xperia.models.UserOauthToken;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


@RestController
@RequestMapping("/api/internal")
public class InternalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalController.class);

    private final InternalService internalService;

    @Autowired
    public InternalController(InternalService internalService){
        this.internalService = internalService;
    }

    @GetMapping(value = "/users/google", produces = "application/json")
    public ResponseEntity<AbstractResponse> fetchGoogleDetailsOfUsers(){
        List<UserOauthToken> validTokens = this.internalService.findUsersWithGoogleAccessToken();
        return ResponseEntity.ok().body(new SuccessResponse(validTokens));
    }

    @GetMapping("/user/settings")
    public ResponseEntity<AbstractResponse> fetchUserSettings(@RequestParam("email") String email,
                                                              @RequestParam("settingsType") String type){

        try{
            email = URLDecoder.decode(email, StandardCharsets.UTF_8);
            SharedUserSetting userSettings = this.internalService.findUserSettingsByType(email, type);
            return ResponseEntity.ok(new SuccessResponse(userSettings));
        }catch (Exception ex){
            LOGGER.error("Error fetching settings for user {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/user/mailDetails")
    public ResponseEntity<AbstractResponse> fetchMailDetails(@RequestParam("email") String email){

        try{
            email = URLDecoder.decode(email, StandardCharsets.UTF_8);
            SharedMailDetails mailDetails = this.internalService.findUserMailDetails(email);
            return ResponseEntity.ok(new SuccessResponse(mailDetails));
        }catch (Exception ex){
            LOGGER.error("Error fetching user mail details for user : {}", email, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error fetching user mail details"));
        }
    }

    @PostMapping(value = "/refresh/token", produces = "application/json")
    public ResponseEntity<AbstractResponse> refreshAccessToken(@RequestParam(value = "email", required = false) String email){
        try{
            email = URLDecoder.decode(email, StandardCharsets.UTF_8);
            UserOauthToken refreshedToken = this.internalService.refreshOAuthToken(email);
            return ResponseEntity.ok().body(new SuccessResponse(refreshedToken));
        }catch (TrackerNotFoundException ex){
            return ResponseEntity.notFound().build();
        }
    }


}
