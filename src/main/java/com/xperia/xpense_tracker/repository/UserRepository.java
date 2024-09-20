package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.TrackerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<TrackerUser, String> {

    Optional<TrackerUser> findByEmail(String email);
}
