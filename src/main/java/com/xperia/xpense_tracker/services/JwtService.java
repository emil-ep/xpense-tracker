package com.xperia.xpense_tracker.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String extractUserName(String token);

    String generateToken(UserDetails userDetails);

    boolean isValidToken(String token, UserDetails userDetails);

}
