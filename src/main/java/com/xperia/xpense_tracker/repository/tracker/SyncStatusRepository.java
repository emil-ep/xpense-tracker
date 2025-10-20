package com.xperia.xpense_tracker.repository.tracker;

import com.xperia.xpense_tracker.models.entities.tracker.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SyncStatusRepository extends JpaRepository<SyncStatus, String> {

    Optional<SyncStatus> findByRequestId(String requestId);
}
