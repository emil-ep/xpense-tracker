package com.xperia.xpense_tracker.exception.customexception;

import org.springframework.http.HttpStatus;

public class TrackerBadRequestException extends TrackerException{

    public TrackerBadRequestException(String message){
        super(message, HttpStatus.BAD_REQUEST.value());
    }


}
