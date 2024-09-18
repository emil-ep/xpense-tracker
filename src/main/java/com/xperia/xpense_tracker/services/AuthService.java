package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.request.SignUpRequest;
import org.apache.coyote.BadRequestException;

public interface AuthService {

    void signInUser(String userName, String password) throws BadRequestException;

    TrackerUser signUpUser(SignUpRequest signUpRequest) throws BadRequestException;


}
