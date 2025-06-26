package com.xperia.xpense_tracker.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "removed_expenses")
@NoArgsConstructor
@Getter
public class RemovedExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yy")
    private LocalDate transactionDate;

    private String description;

    private String bankReferenceNo;

    private Double debit;

    private Double credit;

    private Double closingBalance;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private TrackerUser user;

    public RemovedExpense(String id, LocalDate transactionDate, String description,
                          String bankReferenceNo, Double debit, Double credit, Double closingBalance, TransactionType type,
                          TrackerUser user){
        this.id = id;
        this.transactionDate = transactionDate;
        this.description = description;
        this.bankReferenceNo = bankReferenceNo;
        this.debit = debit;
        this.credit = credit;
        this.closingBalance = closingBalance;
        this.type = type;
        this.user = user;
    }

    public RemovedExpense(Expenses expenses){
        this.id = expenses.getId();
        this.transactionDate = expenses.getTransactionDate();
        this.description = expenses.getDescription();
        this.bankReferenceNo = expenses.getBankReferenceNo();
        this.debit = expenses.getDebit();
        this.credit = expenses.getCredit();
        this.closingBalance = expenses.getClosingBalance();
        this.type = expenses.getType();
        this.user = expenses.getUser();
    }
}
