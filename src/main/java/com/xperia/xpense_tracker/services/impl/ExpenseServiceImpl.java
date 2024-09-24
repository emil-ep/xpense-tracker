package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.fileProcessors.StatementFile;
import com.xperia.xpense_tracker.models.fileProcessors.StatementFileFactory;
import com.xperia.xpense_tracker.services.ExpenseService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private static final Map<Integer, String> headerMap;

     static {
        headerMap = new TreeMap<>();
        headerMap.put(0, "Date");
        headerMap.put(1, "Narration");
        headerMap.put(2, "RefNo");
        headerMap.put(3, "Value Dt");
        headerMap.put(4, "Withdrawal Amt.");
        headerMap.put(5, "Deposit Amt.");
        headerMap.put(6, "Closing Balance");
    }


    @Override
    public void processExpenseFromFile(File file) throws IOException{
        String extension = file.getName().split("\\.")[1];
        StatementFile statementFile = StatementFileFactory.createStatementFile(extension);

        List<HashMap<Integer, Object>> parsedFile = statementFile.parseExpenseFromFile(headerMap, file);
        System.out.println(parsedFile);
    }
}
