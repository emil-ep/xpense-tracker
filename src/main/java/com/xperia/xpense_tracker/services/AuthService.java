package com.xperia.xpense_tracker.services;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


    public void signInUser(String userName, String password) throws BadRequestException{
        if (userName.equals("test") && password.equals("test")){

        }else{
            throw new BadRequestException("Credentials not valid");
        }
    }
}
