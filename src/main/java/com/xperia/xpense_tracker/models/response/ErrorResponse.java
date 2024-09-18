package com.xperia.xpense_tracker.models.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class ErrorResponse extends AbstractResponse{


    public ErrorResponse(Object body){
        this.status = 0;
        this.body = body;
    }
}
