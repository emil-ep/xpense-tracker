package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.entities.tracker.UserBankAccount;
import com.xperia.xpense_tracker.models.request.BankAccountRequest;
import com.xperia.xpense_tracker.models.settings.BankAccountType;
import com.xperia.xpense_tracker.repository.tracker.UserBankAccountRepository;
import com.xperia.xpense_tracker.services.UserBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserBankAccountServiceImpl implements UserBankAccountService {

    @Autowired
    private UserBankAccountRepository bankRepository;

    @Override
    public Optional<UserBankAccount> findBankAccount(String bankAccountId, TrackerUser user) {
        return bankRepository.findByIdAndUser(bankAccountId, user);
    }

    @Override
    public Optional<List<UserBankAccount>> findBankAccountsOfUser(TrackerUser user) {
        return bankRepository.findAllByUser(user);
    }

    @Override
    public void saveBankAccount(TrackerUser user, BankAccountRequest request) {
        BankAccountType type = BankAccountType.findByShortName(request.getType());
        UserBankAccount bankAccount = new UserBankAccount(
                request.getName(),
                type == null ? BankAccountType.NONE : type,
                user);
        bankRepository.save(bankAccount);
    }

    @Override
    public void removeBankAccount(String bankAccountId) {
        bankRepository.deleteById(bankAccountId);
    }
}
