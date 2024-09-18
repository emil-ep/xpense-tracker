package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.models.request.SignInRequest;
import com.xperia.xpense_tracker.models.request.SignUpRequest;
import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.services.AuthService;
import com.xperia.xpense_tracker.services.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signIn")
    public ResponseEntity<AbstractResponse> signIn(@RequestBody SignInRequest signInRequest) {
        try{
            authService.signInUser(signInRequest.getUsername(), signInRequest.getPassword());
        }catch (Exception ex){
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }
        return ResponseEntity.ok(null);
    }

    @PutMapping("/signUp")
    public ResponseEntity<AbstractResponse> signUp(@RequestBody SignUpRequest signUpRequest){
        try{
            return ResponseEntity.ok(new SuccessResponse(authService.signUpUser(signUpRequest)));
        }catch (Exception ex){
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }
    }
}
