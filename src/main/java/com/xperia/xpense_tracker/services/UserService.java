package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.TrackerUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService {

    UserDetailsService userDetailsService();

    Optional<TrackerUser> findUserByEmail(String email);
}
