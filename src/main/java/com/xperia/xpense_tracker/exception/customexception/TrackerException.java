package com.xperia.xpense_tracker.exception.customexception;

import lombok.Getter;

@Getter
public class TrackerException extends RuntimeException{

    private int httpStatus;

    public TrackerException(String message, int httpStatus){
        super(message);
        this.httpStatus = httpStatus;
    }


}
