package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.entities.tracker.UserBankAccount;
import com.xperia.xpense_tracker.models.request.BankAccountRequest;

import java.util.List;
import java.util.Optional;

public interface UserBankAccountService {

    Optional<UserBankAccount> findBankAccount(String bankAccountId, TrackerUser user);

    Optional<List<UserBankAccount>> findBankAccountsOfUser(TrackerUser user);

    void upsertBankAccount(TrackerUser user, BankAccountRequest request);

    void removeBankAccount(String bankAccountId);
}
