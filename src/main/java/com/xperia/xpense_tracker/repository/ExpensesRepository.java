package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExpensesRepository extends JpaRepository<Expenses, String> {

    List<Expenses> getExpensesByUser(TrackerUser user);

    Optional<Expenses> findExpensesById(String expenseId);

    Page<Expenses> getPaginatedExpensesByUser(TrackerUser user, Pageable pageable);

    @Query("SELECT e FROM expenses e WHERE e.user = :user AND e.transactionDate IN :dates AND e.bankReferenceNo IN :bankReferenceNos")
    List<Expenses> findByUserAndTransactionDateInAndBankReferenceNoIn(TrackerUser user, Set<LocalDate> dates, Set<String> bankReferenceNos);

    @Query("SELECT " +
            "EXTRACT(YEAR FROM e.transactionDate) AS year, " +
            "EXTRACT(MONTH FROM e.transactionDate) AS month, " +
            "SUM(e.debit) AS totalDebit, " +
            "SUM(e.credit) AS totalCredit " +
            "FROM expenses e " +
            "WHERE e.user = :user " +
            "GROUP BY EXTRACT(YEAR FROM e.transactionDate), EXTRACT(MONTH FROM e.transactionDate) " +
            "ORDER BY EXTRACT(YEAR FROM e.transactionDate), EXTRACT(MONTH FROM e.transactionDate)")
    List<Tuple> findMonthlyDebitSummaries(TrackerUser user);

}
