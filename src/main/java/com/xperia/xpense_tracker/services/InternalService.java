package com.xperia.xpense_tracker.services;


import com.xperia.xpense_tracker.models.entities.tracker.Oauth2Token;

import java.util.List;

public interface InternalService {

    List<Oauth2Token> findUsersWithGoogleAccessToken();
}
