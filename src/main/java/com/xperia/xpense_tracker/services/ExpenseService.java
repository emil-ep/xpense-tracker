package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.request.StatementPreviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ExpenseService {

    Page<Expenses> getExpenses(UserDetails userDetails, PageRequest pageRequest);

    List<Expenses> processExpenseFromFile(File file,
                                          StatementPreviewRequest request,
                                          UserDetails userDetails,
                                          boolean isPreview)
            throws IOException;

}
