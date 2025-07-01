package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.RemovedExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RemovedExpensesRepository extends JpaRepository<RemovedExpense, String> {

    Optional<RemovedExpense> findByBankReferenceNo(String bankReferenceNo);
}
