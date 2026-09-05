package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.tracker.Oauth2Token;
import com.xperia.xpense_tracker.services.InternalService;
import com.xperia.xpense_tracker.services.Oauth2TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xperia.exception.TrackerNotFoundException;
import org.xperia.models.UserOauthToken;

import java.util.List;
import java.util.Optional;

@Service
public class InternalServiceImpl implements InternalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalServiceImpl.class);

    private final Oauth2TokenService tokenService;

    @Autowired
    public InternalServiceImpl(Oauth2TokenService tokenService){
        this.tokenService = tokenService;
    }

    @Override
    public List<UserOauthToken> findUsersWithGoogleAccessToken() {
        return this.tokenService.findAllValidTokens();
    }

    @Override
    public void refreshOAuthToken(String email) throws TrackerNotFoundException{
        Optional< Oauth2Token> userToken = this.tokenService.getToken(email);
        if (userToken.isEmpty()){
            LOGGER.error("No auth token found for user : {}", email);
            throw new TrackerNotFoundException("No Auth token found for the user " + email);
        }
        this.tokenService.refreshAndSaveToken(userToken.get());
    }
}
