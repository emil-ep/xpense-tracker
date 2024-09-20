package com.xperia.xpense_tracker.models.response;

public class SuccessResponse extends AbstractResponse{

    public SuccessResponse(Object data) {
        this.status = 1;
        this.data = data;
    }
}
