package com.xperia.xpense_tracker.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}", message = "Date must be in the format dd/MM/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate transactionDate;

    private String description;

    private String bankReferenceNo;

    private Float debit;

    private Float credit;

    private Float closingBalance;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private Expenses(ExpenseBuilder builder){
        this.description = builder.description;
        this.bankReferenceNo = builder.bankReferenceNo;
        this.debit = builder.debit;
        this.credit = builder.credit;
        this.closingBalance = builder.closingBalance;
        this.transactionDate = builder.transactionDate;
        this.type = builder.type;
    }

    public static class ExpenseBuilder{

        private LocalDate transactionDate;

        private String description;

        private String bankReferenceNo;

        private Float debit;

        private Float credit;

        private Float closingBalance;

        private TransactionType type;

        public ExpenseBuilder(){

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

        public ExpenseBuilder setCredit(Float credit){
            this.credit = credit;
            return this;
        }

        public ExpenseBuilder setDebit(Float debit){
            this.debit = debit;
            return this;
        }

        public ExpenseBuilder setClosingBalance(Float closingBalance){
            this.closingBalance = closingBalance;
            return this;
        }

        public Expenses build(){
            if (this.credit == null && this.debit != null){
                this.type = TransactionType.DEBIT;
            }else{
                this.type = TransactionType.CREDIT;
            }
            return new Expenses(this);
        }
    }
}
