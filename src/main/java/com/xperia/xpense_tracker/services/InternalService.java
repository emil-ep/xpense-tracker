package com.xperia.xpense_tracker.services;


import com.xperia.xpense_tracker.models.entities.tracker.MailDetails;
import org.xperia.models.SharedMailDetails;
import org.xperia.models.SharedUserSetting;
import org.xperia.models.UserOauthToken;

import java.util.List;

public interface InternalService {

    List<UserOauthToken> findUsersWithGoogleAccessToken();

    UserOauthToken refreshOAuthToken(String email);

    SharedUserSetting findUserSettingsByType(String userEmail, String type);

    SharedMailDetails findUserMailDetails(String userEmail);
}
