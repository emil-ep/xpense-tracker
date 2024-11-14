package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ExpensesRepository extends JpaRepository<Expenses, String> {

    List<Expenses> getExpensesByUser(TrackerUser user);

    Page<Expenses> getPaginatedExpensesByUser(TrackerUser user, Pageable pageable);

    @Query("SELECT e FROM expenses e WHERE e.user = :user AND e.transactionDate IN :dates AND e.bankReferenceNo IN :bankReferenceNos")
    List<Expenses> findByUserAndTransactionDateInAndBankReferenceNoIn(TrackerUser user, Set<LocalDate> dates, Set<String> bankReferenceNos);

    @Query("SELECT " +
            "CASE WHEN :aggregateBy = 'daily' THEN DATE(e.transactionDate) " +
            "     WHEN :aggregateBy = 'monthly' THEN FUNCTION('MONTH', e.transactionDate) " +
            "     WHEN :aggregateBy = 'yearly' THEN FUNCTION('YEAR', e.transactionDate) " +
            "END AS period, " +
            "SUM(e.debit) AS totalAmount " +
            "FROM expenses e " +
            "GROUP BY period " +
            "ORDER BY period ASC")
    List<Object[]> findAggregatedExpensesByPeriod(String aggregateBy);
}
