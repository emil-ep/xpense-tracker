package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.entities.UserRole;
import com.xperia.xpense_tracker.models.request.SignUpRequest;
import com.xperia.xpense_tracker.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{


    @Autowired
    private UserRepository userRepository;

    @Override
    public void signInUser(String userName, String password) throws BadRequestException{

        if(userRepository.findByEmail(userName).isEmpty()){
            throw new BadRequestException("User with the provided email not present");
        }

        if (userName.equals("test") && password.equals("test")){

        }else{
            throw new BadRequestException("Credentials not valid");
        }
    }

    private void validateSignUp(String email) throws BadRequestException{

        if(userRepository.findByEmail(email).isPresent()){
            throw new BadRequestException("User with the same email already present");
        }

    }

    public TrackerUser signUpUser(SignUpRequest signUpRequest) throws BadRequestException{
        validateSignUp(signUpRequest.getEmail());
        return userRepository.save(new TrackerUser(signUpRequest.getEmail(),
                signUpRequest.getPassword(), signUpRequest.getName(), UserRole.USER));
    }
}
