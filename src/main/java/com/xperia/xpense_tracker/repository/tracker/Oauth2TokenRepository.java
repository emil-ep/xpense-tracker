package com.xperia.xpense_tracker.repository.tracker;

import com.xperia.xpense_tracker.models.entities.tracker.Oauth2Token;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Oauth2TokenRepository extends JpaRepository<Oauth2Token, String> {

    Optional<Oauth2Token> findByUser(TrackerUser user);
}
