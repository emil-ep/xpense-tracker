package com.xperia.xpense_tracker.models.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class SuccessResponse extends AbstractResponse{

    public SuccessResponse(Object body) {
        this.status = 1;
        this.body = body;
    }
}
