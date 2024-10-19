package com.xperia.xpense_tracker.exception.customexception;

import org.springframework.http.HttpStatus;

public class TrackerUnknownException extends TrackerException{

    public TrackerUnknownException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
