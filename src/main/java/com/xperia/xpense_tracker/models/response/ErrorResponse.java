package com.xperia.xpense_tracker.models.response;

public class ErrorResponse extends AbstractResponse{


    public ErrorResponse(Object body){
        this.status = 0;
        this.body = body;
    }
}
