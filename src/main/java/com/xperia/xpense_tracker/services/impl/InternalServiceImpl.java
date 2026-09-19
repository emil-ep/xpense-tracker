package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.tracker.MailDetails;
import com.xperia.xpense_tracker.models.entities.tracker.Oauth2Token;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.entities.tracker.UserSettings;
import com.xperia.xpense_tracker.models.settings.SettingsType;
import com.xperia.xpense_tracker.services.InternalService;
import com.xperia.xpense_tracker.services.Oauth2TokenService;
import com.xperia.xpense_tracker.services.UserService;
import com.xperia.xpense_tracker.services.UserSettingsService;
import com.xperia.xpense_tracker.services.MailDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xperia.exception.TrackerBadRequestException;
import org.xperia.exception.TrackerNotFoundException;
import org.xperia.models.SharedMailDetails;
import org.xperia.models.SharedUserSetting;
import org.xperia.models.UserOauthToken;

import java.util.List;
import java.util.Optional;

@Service
public class InternalServiceImpl implements InternalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalServiceImpl.class);

    private final Oauth2TokenService tokenService;

    private final UserSettingsService userSettingsService;

    private final UserService userService;

    private final MailDetailsService mailDetailsService;

    @Autowired
    public InternalServiceImpl(Oauth2TokenService tokenService,
                               UserSettingsService userSettingsService,
                               UserService userService,
                               MailDetailsService mailDetailsService){
        this.tokenService = tokenService;
        this.userSettingsService = userSettingsService;
        this.userService = userService;
        this.mailDetailsService = mailDetailsService;
    }

    @Override
    public List<UserOauthToken> findUsersWithGoogleAccessToken() {
        return this.tokenService.findAllValidTokens();
    }

    @Override
    public UserOauthToken refreshOAuthToken(String email) throws TrackerNotFoundException{
        Optional<Oauth2Token> userToken = this.tokenService.getToken(email);
        if (userToken.isEmpty()){
            LOGGER.error("No auth token found for user : {}", email);
            throw new TrackerNotFoundException("No Auth token found for the user " + email);
        }
        Oauth2Token refreshedToken = this.tokenService.refreshAndSaveToken(userToken.get());
        return new UserOauthToken(
                refreshedToken.getId(),
                refreshedToken.getAccessToken(),
                refreshedToken.getRefreshToken(),
                refreshedToken.getExpireTimestamp(),
                refreshedToken.getUser().getId(),
                refreshedToken.getUser().getEmail());
    }

    @Override
    public SharedUserSetting findUserSettingsByType(String userEmail, String type) {

        Optional<TrackerUser> user = userService.findUserByEmail(userEmail);
        if (user.isEmpty()){
            throw new TrackerBadRequestException("User not found with the email " + userEmail);
        }
        UserSettings userSettings = userSettingsService.findUserSettingsByType(SettingsType.findByType(type), userEmail);
        return new SharedUserSetting(
                userSettings.getId(),
                userSettings.getType().getType(),
                userSettings.getUser().getEmail(),
                userSettings.getUser().getId(),
                userSettings.getPayload());
    }

    @Override
    public SharedMailDetails findUserMailDetails(String userEmail) {
        Optional<TrackerUser> user = userService.findUserByEmail(userEmail);
        if (user.isEmpty()){
            throw new TrackerBadRequestException("User not found with the email " + userEmail);
        }
        Optional<MailDetails> mailDetails = this.mailDetailsService.findMailDetailsByUserId(user.get().getId());
        if (mailDetails.isPresent()){
            MailDetails userMailDetail = mailDetails.get();
            return new SharedMailDetails(
                    userMailDetail.getHistoryId(),
                    userMailDetail.getLastSynced(),
                    userMailDetail.getUser().getId(),
                    userMailDetail.getUser().getEmail());
        }
        LOGGER.error("mail details not found for user : {}", userEmail);
        throw new TrackerNotFoundException("mail details not found for user " + userEmail);
    }
}
