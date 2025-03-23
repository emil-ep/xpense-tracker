package com.xperia.xpense_tracker.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class TimeframeServiceRequest {

    private LocalDate fromDate;

    private LocalDate toDate;

    @Override
    public String toString() {
        return "TimeframeServiceRequest{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }
}
