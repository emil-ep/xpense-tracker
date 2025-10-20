package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.request.SignInRequest;
import com.xperia.xpense_tracker.models.request.SignUpRequest;
import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.models.response.LoginResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/signIn", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/signIn", produces = "application/json")
    public ResponseEntity<AbstractResponse> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        try{
            String token = authService.signInUser(signInRequest.getUsername(), signInRequest.getPassword());
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new SuccessResponse(new LoginResponse(token)));
        } catch (Exception ex){
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }
    }

    @PostMapping(value = "/signUp", produces = "application/json")
    public ResponseEntity<AbstractResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest){
        try{
            TrackerUser savedUser = authService.signUpUser(signUpRequest);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new SuccessResponse(savedUser));
        }catch (Exception ex){
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(ex.getMessage()));
        }
    }
}
