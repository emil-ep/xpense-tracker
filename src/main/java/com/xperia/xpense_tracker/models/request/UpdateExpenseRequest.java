package com.xperia.xpense_tracker.models.request;

import com.xperia.xpense_tracker.models.entities.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateExpenseRequest {

    private String description;

    private LocalDate transactionDate;

    private String bankReferenceNo;

    private Double credit;

    private Double debit;

    private Double closingBalance;

    private Set<Tag> tags;


}
