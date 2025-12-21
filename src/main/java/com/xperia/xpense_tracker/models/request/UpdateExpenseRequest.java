package com.xperia.xpense_tracker.models.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class UpdateExpenseRequest {

    private String description;

    private LocalDate transactionDate;

    private String bankReferenceNo;

    private Double credit;

    private Double debit;

    private Double closingBalance;

    private Set<String> tagIds;

    private String attachment;

    private String notes;


}
