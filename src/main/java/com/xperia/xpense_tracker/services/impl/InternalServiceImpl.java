package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.tracker.Oauth2Token;
import com.xperia.xpense_tracker.services.InternalService;
import com.xperia.xpense_tracker.services.Oauth2TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternalServiceImpl implements InternalService {

    private final Oauth2TokenService tokenService;

    @Autowired
    public InternalServiceImpl(Oauth2TokenService tokenService){
        this.tokenService = tokenService;
    }

    @Override
    public List<Oauth2Token> findUsersWithGoogleAccessToken() {
        return this.tokenService.findAllValidTokens();
    }
}
