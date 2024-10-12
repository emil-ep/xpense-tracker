package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpensesRepository extends JpaRepository<Expenses, String> {

    List<Expenses> getExpensesByUser(TrackerUser user);
}
