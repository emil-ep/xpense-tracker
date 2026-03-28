package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.tracker.Expenses;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.entities.tracker.UserBankAccount;
import com.xperia.xpense_tracker.models.request.BankAccountRequest;
import com.xperia.xpense_tracker.models.settings.BankAccountType;
import com.xperia.xpense_tracker.repository.tracker.ExpensesRepository;
import com.xperia.xpense_tracker.repository.tracker.UserBankAccountRepository;
import com.xperia.xpense_tracker.services.UserBankAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    private ExpensesRepository expensesRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBankAccountServiceImpl.class);

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
    public void removeBankAccount(TrackerUser user, UserBankAccount bankAccount) {
        List<Expenses> attachedExpenses = expensesRepository.getExpensesByUserAndBankAccount(user, bankAccount);
        if (attachedExpenses.isEmpty()){
            bankRepository.deleteById(bankAccount.getId());
        }
        LOGGER.debug("Total : {} expenses are attached to the bank account : {}", attachedExpenses.size(), bankAccount.getId());
        throw new TrackerBadRequestException("Expenses are attached to this bank account. " +
                "Please assign to different account or delete expenses first");
    }

    @Override
    public List<String> fetchBankAccountTypes() {
        return Arrays.stream(BankAccountType.values()).map(BankAccountType::getShortName).toList();
    }
}
