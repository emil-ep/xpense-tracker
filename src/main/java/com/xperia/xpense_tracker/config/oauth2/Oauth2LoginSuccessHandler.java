package com.xperia.xpense_tracker.config.oauth2;

import com.xperia.xpense_tracker.services.Oauth2TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private Oauth2TokenService oauth2TokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        String accessToken  = client.getAccessToken().getTokenValue();
        Instant expiresAt   = client.getAccessToken().getExpiresAt();
        String refreshToken = client.getRefreshToken() != null
                ? client.getRefreshToken().getTokenValue()
                : null;

        String userEmail = oauthToken.getPrincipal().getAttribute("email");
        oauth2TokenService.saveToken(userEmail, accessToken, refreshToken);
    }
}
