package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.tracker.MailDetails;

import java.util.Optional;

public interface MailDetailsService {

    Optional<MailDetails> findMailDetailsByUserId(String userId);
}
