package com.xperia.xpense_tracker.config;

import com.xperia.xpense_tracker.repository.ExpensesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This is the security package for xpense-tracker.
 * This is used - DO NOT DELETE
 */
@Component("expenseSecurity")
public class ExpenseSecurity {

    @Autowired
    private ExpensesRepository expensesRepository;

    /**
     * This method is referenced from PreAuthorize to determine the owner or not
     * @param expenseId the expense whose owner is to be determined
     * @param username the name of the user - in this case email
     * @return returns true if its owner
     */
    public boolean isOwner(String expenseId, String username){
        return expensesRepository.findExpensesById(expenseId)
                .map(expense -> expense.getUser().getEmail().equals(username))
                .orElse(false);
    }
}
