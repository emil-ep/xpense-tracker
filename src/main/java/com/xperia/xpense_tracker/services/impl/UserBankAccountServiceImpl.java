package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.entities.tracker.UserBankAccount;
import com.xperia.xpense_tracker.models.request.BankAccountRequest;
import com.xperia.xpense_tracker.services.UserBankAccountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserBankAccountServiceImpl implements UserBankAccountService {

    @Override
    public Optional<UserBankAccount> findBankAccount(String bankAccountId, TrackerUser user) {
        return Optional.empty();
    }

    @Override
    public Optional<List<UserBankAccount>> findBankAccountsOfUser(TrackerUser user) {
        return Optional.empty();
    }

    @Override
    public void saveBankAccount(TrackerUser user, BankAccountRequest request) {

    }

    @Override
    public void removeBankAccount(String bankAccountId) {

    }
}
