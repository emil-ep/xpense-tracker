package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.request.StatementPreviewRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ExpenseService {

    List<Expenses> getExpenses(UserDetails userDetails);

    void processExpenseFromFile(File file, StatementPreviewRequest request, UserDetails userDetails) throws IOException;

    List<Expenses> previewExpenses(File file, StatementPreviewRequest request) throws IOException;
}
