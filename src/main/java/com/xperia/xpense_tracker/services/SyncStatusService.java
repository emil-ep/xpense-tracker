package com.xperia.xpense_tracker.services;


import com.xperia.xpense_tracker.models.entities.tracker.SyncStatus;

import java.util.Optional;

public interface SyncStatusService {

    SyncStatus saveStatus(SyncStatus status);

    SyncStatus updateStatus(SyncStatus status);

    Optional<SyncStatus> fetchStatus(String requestId);
}
