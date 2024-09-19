package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.entities.UserRole;
import com.xperia.xpense_tracker.models.request.SignUpRequest;
import com.xperia.xpense_tracker.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    private void validateSignIn(String email) throws BadRequestException {
        if(userRepository.findByEmail(email).isEmpty()){
            throw new BadRequestException("User with the provided email not present");
        }
    }

    @Override
    public void signInUser(String userName, String password) throws BadRequestException{
        validateSignIn(userName);
        var user = userRepository.findByEmail(userName).get();
        if(encoder.matches(password, user.getPassword())){

        }else{
            throw new BadRequestException("Entered username or password doesn't match");
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
                encoder.encode(signUpRequest.getPassword()), signUpRequest.getName(), UserRole.USER));
    }
}
