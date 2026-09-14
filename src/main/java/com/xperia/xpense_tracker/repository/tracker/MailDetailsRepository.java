package com.xperia.xpense_tracker.repository.tracker;

import com.xperia.xpense_tracker.models.entities.tracker.MailDetails;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MailDetailsRepository extends JpaRepository<MailDetails, String> {

    Optional<MailDetails> findMailDetailsByUser(TrackerUser user);
}
