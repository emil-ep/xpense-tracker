package com.xperia.xpense_tracker.exception.customexception;

import org.springframework.http.HttpStatus;

public class TrackerNotFoundException extends TrackerException{

    public TrackerNotFoundException(String message){
        super(message, HttpStatus.NOT_FOUND.value());
    }
}
