package com.xperia.xpense_tracker.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TimeframeRequest {

    private String fromDate;

    private String toDate;
}
