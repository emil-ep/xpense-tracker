package com.xperia.xpense_tracker.repository.tracker;

import com.xperia.xpense_tracker.models.entities.tracker.Oauth2Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Oauth2TokenRepository extends JpaRepository<Oauth2Token, String> {
}
