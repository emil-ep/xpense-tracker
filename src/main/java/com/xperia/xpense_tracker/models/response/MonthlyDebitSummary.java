package com.xperia.xpense_tracker.models.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class MonthlyDebitSummary {

    private int year;
    private int month;
    private double totalDebit;


}
