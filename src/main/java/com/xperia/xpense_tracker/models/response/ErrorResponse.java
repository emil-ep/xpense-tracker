package com.xperia.xpense_tracker.models.response;

public class ErrorResponse extends AbstractResponse{

    public ErrorResponse(Object data){
        this.status = 0;
        this.data = data;
    }
}
