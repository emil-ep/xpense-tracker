package com.xperia.xpense_tracker.models.entities.tracker;

import com.xperia.xpense_tracker.models.settings.BankAccountType;
import jakarta.persistence.*;

@Entity(name = "user_bank_account")
public class UserBankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private BankAccountType type;

    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private TrackerUser user;
}
