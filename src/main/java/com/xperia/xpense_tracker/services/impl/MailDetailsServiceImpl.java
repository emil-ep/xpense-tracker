package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.tracker.MailDetails;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.repository.tracker.MailDetailsRepository;
import com.xperia.xpense_tracker.services.MailDetailsService;
import com.xperia.xpense_tracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xperia.exception.TrackerBadRequestException;

import java.util.Optional;

@Service
public class MailDetailsServiceImpl implements MailDetailsService {

    private final UserService userService;

    private final MailDetailsRepository mailDetailsRepository;

    @Autowired
    public MailDetailsServiceImpl(UserService userService, MailDetailsRepository mailDetailsRepository){
        this.userService = userService;
        this.mailDetailsRepository = mailDetailsRepository;
    }

    @Override
    public Optional<MailDetails> findMailDetailsByUserId(String userId) {
        Optional<TrackerUser> user = this.userService.findUserByUserId(userId);
        if (user.isEmpty()){
            throw new TrackerBadRequestException("User with id : " + userId + " not found");
        }
        return mailDetailsRepository.findMailDetailsByUser(user.get());
    }
}
