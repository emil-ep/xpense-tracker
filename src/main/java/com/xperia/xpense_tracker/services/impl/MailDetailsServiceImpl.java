package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.tracker.MailDetails;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.repository.tracker.MailDetailsRepository;
import com.xperia.xpense_tracker.services.MailDetailsService;
import com.xperia.xpense_tracker.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.xperia.exception.TrackerBadRequestException;
import org.xperia.exception.TrackerException;

import java.util.Optional;

@Service
public class MailDetailsServiceImpl implements MailDetailsService {

    private final UserService userService;

    private final MailDetailsRepository mailDetailsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailDetailsService.class);

    @Autowired
    public MailDetailsServiceImpl(UserService userService, MailDetailsRepository mailDetailsRepository){
        this.userService = userService;
        this.mailDetailsRepository = mailDetailsRepository;
    }

    @Override
    public Optional<MailDetails> findMailDetailsByUserId(String userId) {
        Optional<TrackerUser> user = this.userService.findUserByUserId(userId);
        if (user.isEmpty()){
            LOGGER.error("Error finding mailDetails for the user : {}", userId);
            throw new TrackerBadRequestException("User with id : " + userId + " not found");
        }
        return mailDetailsRepository.findMailDetailsByUser(user.get());
    }

    @Override
    public MailDetails saveMailDetails(MailDetails mailDetailsToUpdate) {
        try{
            return mailDetailsRepository.save(mailDetailsToUpdate);
        }catch (Exception ex){
            LOGGER.error("Error while saving mail details for user : {}, lastSynced : {}, historyId : {}",
                    mailDetailsToUpdate.getUser().getEmail(),
                    mailDetailsToUpdate.getLastSynced(),
                    mailDetailsToUpdate.getHistoryId());
            throw new TrackerException("Error saving mail details for user " + mailDetailsToUpdate.getUser().getEmail(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex);
        }
    }
}
