package com.xperia.xpense_tracker.services;


import org.xperia.models.UserOauthToken;

import java.util.List;

public interface InternalService {

    List<UserOauthToken> findUsersWithGoogleAccessToken();

    void refreshOAuthToken(String email);
}
