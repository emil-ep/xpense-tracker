package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.services.InternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xperia.exception.TrackerNotFoundException;
import org.xperia.models.UserOauthToken;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


@RestController
@RequestMapping("/api/internal")
public class InternalController {

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
