package com.xperia.xpense_tracker.services.security;

import com.xperia.xpense_tracker.repository.ExpensesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("expenseSecurity")
public class ExpenseSecurity {

    @Autowired
    private ExpensesRepository expensesRepository;


    public boolean isOwner(String expenseId, String username){
        return expensesRepository.findExpensesById(expenseId)
                .map(expense -> expense.getUser().getEmail().equals(username))
                .orElse(false);
    }
}
