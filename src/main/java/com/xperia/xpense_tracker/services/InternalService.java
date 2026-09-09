package com.xperia.xpense_tracker.services;


import org.xperia.models.UserOauthToken;

import java.util.List;

public interface InternalService {

    List<UserOauthToken> findUsersWithGoogleAccessToken();

    UserOauthToken refreshOAuthToken(String email);
}
