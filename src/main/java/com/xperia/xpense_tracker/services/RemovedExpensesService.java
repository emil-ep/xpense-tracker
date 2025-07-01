package com.xperia.xpense_tracker.services;

public interface RemovedExpensesService {

    boolean isRemovedExpense(String description, String bankReferenceNumber);
}
