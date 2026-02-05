package com.xperia.xpense_tracker.repository.tracker;

import com.xperia.xpense_tracker.models.entities.tracker.Expenses;
import com.xperia.xpense_tracker.models.entities.tracker.Statements;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.entities.tracker.UserBankAccount;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExpensesRepository extends JpaRepository<Expenses, String> {

    @Query("SELECT e FROM expenses e WHERE e.user = :user " +
            "AND (CAST(:fromDate AS date) IS NULL OR e.transactionDate >= :fromDate)" +
            "AND (CAST(:toDate AS date) IS NULL OR e.transactionDate <= :toDate)")
    List<Expenses> findExpensesByUserAndTransactionDateBetween(
            @Param("user") TrackerUser user,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    List<Expenses> getExpensesByUser(TrackerUser user);

    List<Expenses> getExpensesByUserAndBankAccount(TrackerUser user, UserBankAccount bankAccount);

    Optional<Expenses> findExpensesById(String expenseId);

    @Query("SELECT e FROM expenses e WHERE e.user = :user AND e.transactionDate BETWEEN :fromDate AND :toDate")
    Page<Expenses> getPaginatedExpensesByUser(TrackerUser user, @Param("fromDate") LocalDate fromDate,
                                              @Param("toDate") LocalDate toDate, Pageable pageable);

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

    @Query("SELECT " +
            "CASE\n" +
            "  WHEN :metricTimeFrame = 'daily' THEN TO_CHAR(e.transactionDate, 'YYYY-MM-DD')\n" +
            "  WHEN :metricTimeFrame = 'monthly' THEN TO_CHAR(e.transactionDate, 'YYYY-MM')\n" +
            "  WHEN :metricTimeFrame = 'yearly' THEN TO_CHAR(e.transactionDate, 'YYYY')\n" +
            "END AS timeFrame,\n" +
            "  COALESCE(SUM(e.credit), 0) AS deposits,\n" +
            "  COALESCE(SUM(e.debit), 0) AS expenses\n" +
            "FROM expenses e\n" +
            "WHERE e.transactionDate IS NOT NULL AND e.user = :user\n" +
            "GROUP BY timeFrame\n" +
            "ORDER BY timeFrame")
    List<Object[]> aggregateExpensesByMetricTimeFrame(@Param("metricTimeFrame") String metricTimeFrame, TrackerUser user);

    @Query("SELECT DISTINCT statement FROM expenses")
    List<Statements> findDistinctStatements();
}
