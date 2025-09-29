package com.xperia.xpense_tracker.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "expenses")
@NoArgsConstructor
@Getter
public class Expenses {

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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "expense_tags",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    private Set<Tag> tags = new HashSet<>();

    private String attachment;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Statements statement;

    private String notes;

    private Expenses(ExpenseBuilder builder){
        this.description = builder.description;
        this.bankReferenceNo = builder.bankReferenceNo;
        this.debit = builder.debit;
        this.credit = builder.credit;
        this.closingBalance = builder.closingBalance;
        this.transactionDate = builder.transactionDate;
        this.type = builder.type;
        this.user = builder.user;
        this.tags = builder.tags;
        this.attachment = builder.attachment;
        this.statement = builder.statement;

    }

    private Expenses(String id, ExpenseBuilder builder){
        this.id = id;
        this.description = builder.description;
        this.bankReferenceNo = builder.bankReferenceNo;
        this.debit = builder.debit;
        this.credit = builder.credit;
        this.closingBalance = builder.closingBalance;
        this.transactionDate = builder.transactionDate;
        this.type = builder.type;
        this.user = builder.user;
        this.tags = builder.tags;
        this.attachment = builder.attachment;
        this.notes = builder.notes;
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

        private Set<Tag> tags;

        private String attachment;

        private Statements statement;

        private String notes;

        public ExpenseBuilder(TrackerUser user){
            this.user = user;
        }

        public ExpenseBuilder(Expenses existing){
            this.transactionDate = existing.getTransactionDate();
            this.description = existing.getDescription();
            this.bankReferenceNo = existing.getBankReferenceNo();
            this.debit = existing.getDebit();
            this.credit = existing.getCredit();
            this.closingBalance = existing.getClosingBalance();
            this.type = existing.getType();
            this.tags = new HashSet<>(existing.tags);
            this.user = existing.getUser();
            this.attachment = existing.attachment;
            this.notes = existing.notes;
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

        public ExpenseBuilder withTags(Set<Tag> tags){
            this.tags = tags;
            return this;
        }

        public ExpenseBuilder withAttachment(String attachment){
            this.attachment = attachment;
            return this;
        }

        public ExpenseBuilder ofStatement(Statements statement){
            this.statement = statement;
            return this;
        }

        public ExpenseBuilder withNote(String note){
            this.notes = note;
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

        public Expenses build(String id){
            return new Expenses(id, this);
        }
    }
}
