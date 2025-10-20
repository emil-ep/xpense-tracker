package com.xperia.xpense_tracker.models.entities.tracker;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "sync_status")
@NoArgsConstructor
@Getter
@Setter
public class SyncStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String requestId;

    @Enumerated(EnumType.STRING)
    private SyncStatusEnum status;

    public SyncStatus(String requestId, SyncStatusEnum status){
        this.requestId = requestId;
        this.status = status;
    }

}
