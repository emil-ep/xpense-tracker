package com.xperia.xpense_tracker.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AggregatedExpenseResponse {

    private String timeframe;

    private Double deposits;

    private Double expenses;


}
