package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpensesRepository extends JpaRepository<Expenses, String> {


}
