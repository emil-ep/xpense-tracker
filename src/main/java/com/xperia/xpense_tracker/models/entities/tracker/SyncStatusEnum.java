package com.xperia.xpense_tracker.models.entities.tracker;

import lombok.Getter;

@Getter
public enum SyncStatusEnum {

    IN_PROGRESS(1),
    FAILED(2),
    COMPLETED(3);

    private final Integer status;

    SyncStatusEnum(Integer status){
        this.status = status;
    }
}
