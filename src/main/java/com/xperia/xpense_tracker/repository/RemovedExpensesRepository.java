package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.RemovedExpense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemovedExpensesRepository extends JpaRepository<RemovedExpense, String> {
}
