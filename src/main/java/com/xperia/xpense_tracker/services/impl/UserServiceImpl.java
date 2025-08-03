package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.repository.UserRepository;
import com.xperia.xpense_tracker.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public Optional<TrackerUser> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
