package com.xperia.xpense_tracker.models.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class StatementPreviewRequest {

    private Integer transactionDate;

    private Integer description;

    private Integer bankReferenceNo;

    private Integer debit;

    private Integer credit;

    private Integer closingBalance;

    private Integer headerStartIndex;
    
}
