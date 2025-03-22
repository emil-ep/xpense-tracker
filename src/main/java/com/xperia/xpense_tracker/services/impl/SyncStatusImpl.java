package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.SyncStatus;
import com.xperia.xpense_tracker.repository.SyncStatusRepository;
import com.xperia.xpense_tracker.services.SyncStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SyncStatusImpl implements SyncStatusService {

    @Autowired
    private SyncStatusRepository syncStatusRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncStatusImpl.class);

    @Override
    public SyncStatus saveStatus(SyncStatus status) {
        return syncStatusRepository.save(status);
    }

    @Override
    public SyncStatus updateStatus(SyncStatus status) {
        Optional<SyncStatus> existingSyncStatus = syncStatusRepository.findByRequestId(status.getRequestId());
        if (!existingSyncStatus.isPresent()){
            LOGGER.debug("Sync status not found for requestId : {}", status.getRequestId());
            return this.saveStatus(status);
        }
        existingSyncStatus.get().setStatus(status.getStatus());
        return saveStatus(existingSyncStatus.get());
    }

    @Override
    public Optional<SyncStatus> fetchStatus(String requestId) {
        return syncStatusRepository.findByRequestId(requestId);
    }
}
