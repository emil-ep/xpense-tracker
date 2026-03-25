package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.entities.tracker.UserBankAccount;
import com.xperia.xpense_tracker.models.request.BankAccountRequest;
import com.xperia.xpense_tracker.models.settings.BankAccountType;
import com.xperia.xpense_tracker.repository.tracker.UserBankAccountRepository;
import com.xperia.xpense_tracker.services.UserBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xperia.exception.TrackerBadRequestException;

import java.util.Arrays;
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
    public void upsertBankAccount(TrackerUser user, BankAccountRequest request) {
        BankAccountType accountType = BankAccountType.findByShortName(request.getType());
        if (accountType == null){
            throw new TrackerBadRequestException("bank account type is not valid");
        }
        UserBankAccount bankAccount;
        if (request.getId() != null && !request.getId().isEmpty()){
            Optional<UserBankAccount> bankAccountOptional = bankRepository.findByIdAndUser(request.getId(), user);
            if (bankAccountOptional.isEmpty()){
                throw new TrackerBadRequestException("bank id not valid or not associated to this user");
            }
            bankAccount = bankAccountOptional.get();
            bankAccount.setAccountNumber(request.getAccountNumber());
            bankAccount.setName(request.getName());
            bankAccount.setType(accountType);
        }else{
            bankAccount = new UserBankAccount(
                    request.getName(),
                    request.getType() == null ? BankAccountType.NONE : BankAccountType.findByShortName(request.getType()),
                    user);
        }
        bankRepository.save(bankAccount);
    }

    @Override
    public void removeBankAccount(String bankAccountId) {
        //TODO Implement checks to not allow deletion if expenses are attached
        //TODO should be done next on priority 25/3/2026
        bankRepository.deleteById(bankAccountId);
    }

    @Override
    public List<String> fetchBankAccountTypes() {
        return Arrays.stream(BankAccountType.values()).map(BankAccountType::getShortName).toList();
    }
}
