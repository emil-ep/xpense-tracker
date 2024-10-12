package com.xperia.xpense_tracker.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "expenses")
@NoArgsConstructor
@Getter
public class Expenses {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

//    @Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}", message = "Date must be in the format dd/MM/yy")
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

    private Expenses(ExpenseBuilder builder){
        this.description = builder.description;
        this.bankReferenceNo = builder.bankReferenceNo;
        this.debit = builder.debit;
        this.credit = builder.credit;
        this.closingBalance = builder.closingBalance;
        this.transactionDate = builder.transactionDate;
        this.type = builder.type;
        this.user = builder.user;
    }

    public static class ExpenseBuilder{

        private LocalDate transactionDate;

        private String description;

        private String bankReferenceNo;

        private Double debit;

        private Double credit;

        private Double closingBalance;

        private TransactionType type;

        private TrackerUser user;

        public ExpenseBuilder(TrackerUser user){
            this.user = user;
        }

        public ExpenseBuilder onDate(LocalDate transactionDate){
            this.transactionDate = transactionDate;
            return this;
        }

        public ExpenseBuilder withDescription(String description){
            this.description = description;
            return this;
        }

        public ExpenseBuilder withBankReferenceNo(String bankReferenceNo){
            this.bankReferenceNo = bankReferenceNo;
            return this;
        }

        public ExpenseBuilder setCredit(Double credit){
            this.credit = credit;
            return this;
        }

        public ExpenseBuilder setDebit(Double debit){
            this.debit = debit;
            return this;
        }

        public ExpenseBuilder setClosingBalance(Double closingBalance){
            this.closingBalance = closingBalance;
            return this;
        }

        public Expenses build(){
            if (this.credit == 0.0 && this.debit != 0.0){
                this.type = TransactionType.DEBIT;
            }else{
                this.type = TransactionType.CREDIT;
            }
            return new Expenses(this);
        }
    }
}
