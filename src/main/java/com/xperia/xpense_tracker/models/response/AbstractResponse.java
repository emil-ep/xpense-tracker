package com.xperia.xpense_tracker.models.response;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public abstract class AbstractResponse {

    protected Object body;

    protected int status;
}
