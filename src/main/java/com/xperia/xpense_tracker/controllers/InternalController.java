package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.services.InternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
        this.internalService.findUsersWithGoogleAccessToken();
        return ResponseEntity.ok().body(new SuccessResponse(null));
    }
}
