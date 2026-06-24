package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.tracker.Oauth2Token;

import java.util.Optional;

public interface Oauth2TokenService {

    void saveToken(String email, String accessToken, String refreshToken, Long expireTimestamp);

    Optional<Oauth2Token> getToken(String email);
}
