package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.tracker.RemovedExpense;
import com.xperia.xpense_tracker.repository.tracker.RemovedExpensesRepository;
import com.xperia.xpense_tracker.services.RemovedExpensesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RemovedServicesImpl implements RemovedExpensesService {

    @Autowired
    private RemovedExpensesRepository repository;


    @Override
    public boolean isRemovedExpense(String description,  String bankReferenceNumber) {

        Optional<RemovedExpense> removedExpense = repository.findByBankReferenceNo(bankReferenceNumber);
        if (removedExpense.isEmpty()) {
            return false;
        }
        return true;
    }
}
