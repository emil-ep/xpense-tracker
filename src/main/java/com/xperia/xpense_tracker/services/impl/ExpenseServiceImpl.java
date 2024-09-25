package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.fileProcessors.FileProcessor;
import com.xperia.xpense_tracker.models.fileProcessors.FileProcessorFactory;
import com.xperia.xpense_tracker.repository.ExpensesRepository;
import com.xperia.xpense_tracker.services.ExpenseService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private static final Map<Integer, String> headerMap;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

    @Autowired
    private ExpensesRepository expensesRepository;

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
        FileProcessor fileProcessor = FileProcessorFactory.createFileProcessor(extension);
        if (fileProcessor == null){
            throw new BadRequestException("File format is invalid. Please upload xlsx files only");
        }
        List<HashMap<Integer, String>> parsedFile = fileProcessor.parseFile(file);

        List<Expenses> expensesList = parsedFile.stream().map(row ->
                        new Expenses.ExpenseBuilder()
                                .onDate(LocalDate.parse(String.valueOf(row.get(0)), formatter))
                                .withDescription(row.get(1))
                                .withBankReferenceNo(row.get(2))
                                .setDebit(row.get(4) != null ? Double.parseDouble(row.get(4)) : 0.0)
                                .setCredit(row.get(5) != null ? Double.parseDouble(row.get(5)) : 0.0)
                                .setClosingBalance(row.get(6) != null ? Double.parseDouble(row.get(6)) : 0.0)
                                .build())
                .toList();
        expensesRepository.saveAll(expensesList);
    }


}
