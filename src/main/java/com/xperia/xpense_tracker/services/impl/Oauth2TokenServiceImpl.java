package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.tracker.Oauth2Token;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.repository.tracker.Oauth2TokenRepository;
import com.xperia.xpense_tracker.services.Oauth2TokenService;
import com.xperia.xpense_tracker.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xperia.client.GoogleClient;
import org.xperia.exception.TrackerBadRequestException;
import org.xperia.models.GoogleTokenResponse;
import org.xperia.models.UserOauthToken;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class Oauth2TokenServiceImpl implements Oauth2TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Oauth2TokenServiceImpl.class);

    private final ExecutorService tokenRefreshExecutor = Executors.newFixedThreadPool(10);

    private final GoogleClient googleClient;

    private final Oauth2TokenRepository oauth2TokenRepository;

    private final UserService userService;

    @Value("$spring.security.oauth2.client.registration.google.client-id")
    private String googleClientId;

    @Value("spring.security.oauth2.client.registration.google.client-secret")
    private String googleClientSecret;

    @Autowired
    public Oauth2TokenServiceImpl(Oauth2TokenRepository oauth2TokenRepository,
                                  UserService userService,
                                  GoogleClient googleClient
    ){
        this.oauth2TokenRepository = oauth2TokenRepository;
        this.userService = userService;
        this.googleClient = googleClient;
    }


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
            token.setExpireTimestamp(expireTimestamp);
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
        return oauth2TokenRepository.findByUser(user.get());
    }

    @Override
    public List<UserOauthToken> findAllValidTokens() {
        List<Oauth2Token> tokens = oauth2TokenRepository.findAll();
        return tokens.stream()
                .map(token -> new UserOauthToken(
                        token.getId(),
                        token.getAccessToken(),
                        token.getRefreshToken(),
                        token.getExpireTimestamp(),
                        token.getUser().getId(),
                        token.getUser().getEmail()
                )).toList();
//        Long currentTimestamp = System.currentTimeMillis();
//        List<CompletableFuture<Oauth2Token>> futures = tokens.stream()
//                .map(token -> {
//                    if (token.getExpireTimestamp() > currentTimestamp){
//                        return CompletableFuture.completedFuture(token);
//                    }
//                    return CompletableFuture.supplyAsync(
//                            () -> refreshAndSaveToken(token), tokenRefreshExecutor
//                    );
//                }).toList();
//        return futures
//                .stream()
//                .map(CompletableFuture::join)
//                .filter(Objects::nonNull)
//                .map(token -> new UserOauthToken(
//                        token.getId(),
//                        token.getAccessToken(),
//                        token.getRefreshToken(),
//                        token.getExpireTimestamp(),
//                        token.getUser().getId(),
//                        token.getUser().getEmail())
//                )
//                .toList();
    }

    @Override
    public Oauth2Token refreshAndSaveToken(Oauth2Token token){
        try{
            GoogleTokenResponse tokenResponse = this.googleClient
                    .refreshAccessToken(token.getRefreshToken(), googleClientId, googleClientSecret);
            token.setAccessToken(tokenResponse.getAccessToken());
            token.setExpireTimestamp(System.currentTimeMillis() + (tokenResponse.getExpiresIn() * 1000));
            if (StringUtils.isNotBlank(tokenResponse.getRefreshToken())) {
                token.setRefreshToken(tokenResponse.getRefreshToken());
            }
            return oauth2TokenRepository.save(token);
        }catch (Exception ex){
            LOGGER.error("Error refreshing token : {}", token.getId(), ex);
            return null;
        }
    }
}
