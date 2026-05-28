package com.xperia.xpense_tracker.services;

public interface Oauth2TokenService {

    void saveToken(String email, String accessToken, String refreshToken);
}
