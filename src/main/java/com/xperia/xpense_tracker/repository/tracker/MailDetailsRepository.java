package com.xperia.xpense_tracker.repository.tracker;

import com.xperia.xpense_tracker.models.entities.tracker.MailDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailDetailsRepository extends JpaRepository<MailDetails, String> {
}
