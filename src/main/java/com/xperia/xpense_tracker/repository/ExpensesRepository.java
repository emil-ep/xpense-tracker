package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ExpensesRepository extends JpaRepository<Expenses, String> {

    List<Expenses> getExpensesByUser(TrackerUser user);

    @Query("SELECT e FROM expenses e WHERE e.user = :user AND e.transactionDate IN :dates AND e.bankReferenceNo IN :bankReferenceNos")
    List<Expenses> findByUserAndTransactionDateInAndBankReferenceNoIn(TrackerUser user, Set<LocalDate> dates, Set<String> bankReferenceNos);
}
