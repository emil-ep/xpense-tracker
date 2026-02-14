package com.xperia.xpense_tracker.services;

public interface RemovedExpensesService {

    //TODO Add trackerUser and bankAccount while filtering - Let's do it step by step (Also add a composite index )
    boolean isRemovedExpense(String description, String bankReferenceNumber);
}
