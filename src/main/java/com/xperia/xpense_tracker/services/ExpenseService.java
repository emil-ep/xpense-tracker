package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.tracker.Expenses;
import com.xperia.xpense_tracker.models.entities.tracker.Statements;
import com.xperia.xpense_tracker.models.request.StatementPreviewRequest;
import com.xperia.xpense_tracker.models.request.UpdateExpenseRequest;
import com.xperia.xpense_tracker.models.response.MonthlyDebitSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseService {

    Page<Expenses> getExpenses(UserDetails userDetails, LocalDate fromDate, LocalDate toDate, PageRequest pageRequest);

    List<Expenses> processExpenseFromFile(File file,
                                          StatementPreviewRequest request,
                                          UserDetails userDetails,
                                          boolean isPreview)
            throws IOException;

    boolean isValidExpenseOfUser(UserDetails userDetails, String expenseId);

    Expenses updateExpense(String expenseId, UpdateExpenseRequest expenseRequest, UserDetails userDetails);

    List<MonthlyDebitSummary> aggregateExpenses(String by, UserDetails userDetails);

    void syncExpenses(UserDetails userDetails, String requestId);

    void syncExpensesV2(UserDetails userDetails, String requestId);

    List<Expenses> listAll();

    void softDeleteExpense(String id, UserDetails userDetails);

    Map<String, String> matchHeaders(List<String> headers);

    List<Statements> findDistinctStatementsOfExpenses();
}
