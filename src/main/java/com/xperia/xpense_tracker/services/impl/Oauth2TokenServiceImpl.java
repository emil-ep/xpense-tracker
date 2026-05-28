package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.tracker.Oauth2Token;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.repository.tracker.Oauth2TokenRepository;
import com.xperia.xpense_tracker.services.Oauth2TokenService;
import com.xperia.xpense_tracker.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xperia.exception.TrackerBadRequestException;

import java.util.Objects;
import java.util.Optional;

@Service
public class Oauth2TokenServiceImpl implements Oauth2TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Oauth2TokenServiceImpl.class);

    @Autowired
    private Oauth2TokenRepository oauth2TokenRepository;

    @Autowired
    private UserService userService;


    @Override
    public void saveToken(String email, String accessToken, String refreshToken, Long expireTimestamp) {
        if (Objects.isNull(email) || email.isEmpty()){
            throw new TrackerBadRequestException("Email received is either empty or null");
        }
        Optional<TrackerUser> user = userService.findUserByEmail(email);
        if (user.isEmpty()){
            LOGGER.error("The received email doesn't have a corresponding user in database : {}", email);
            throw new TrackerBadRequestException("The received email doesn't have a corresponding user in database : " + email);
        }

        Optional<Oauth2Token> existingToken = getToken(email);
        Oauth2Token token;
        if (existingToken.isEmpty()){
            token = new Oauth2Token(accessToken, refreshToken, user.get(), expireTimestamp);
        }else{
            token = existingToken.get();
            token.setRefreshToken(refreshToken);
            token.setAccessToken(accessToken);
        }
        oauth2TokenRepository.save(token);

    }

    @Override
    public Optional<Oauth2Token> getToken(String email) {
        Optional<TrackerUser> user = userService.findUserByEmail(email);
        if (user.isEmpty()){
            LOGGER.error("There is no user with email : {}", email);
            throw new TrackerBadRequestException("There is no user with the email : " + email);
        }
        return oauth2TokenRepository.findByEmail(email);
    }
}
