package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.fileProcessors.FileProcessor;
import com.xperia.xpense_tracker.models.fileProcessors.FileProcessorFactory;
import com.xperia.xpense_tracker.models.request.StatementPreviewRequest;
import com.xperia.xpense_tracker.repository.ExpensesRepository;
import com.xperia.xpense_tracker.services.ExpenseService;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private static final Map<Integer, String> headerMap;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseServiceImpl.class);

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
    public List<Expenses> getExpenses(UserDetails userDetails) {

         TrackerUser user = (TrackerUser) userDetails;
         return expensesRepository.getExpensesByUser(user);
    }

    @Override
    public void processExpenseFromFile(File file, UserDetails userDetails) throws IOException{
        String extension = file.getName().split("\\.")[1];
        FileProcessor fileProcessor = FileProcessorFactory.createFileProcessor(extension);
        if (fileProcessor == null){
            throw new BadRequestException("File format is invalid. Please upload xlsx files only");
        }
        List<HashMap<Integer, String>> parsedFile;
        try{
            parsedFile = fileProcessor.parseFile(file);
        }catch (TrackerBadRequestException ex){
            LOGGER.error("unable to parse the file : {}", ex.getMessage());
            throw ex;
        }

        TrackerUser user = (TrackerUser) userDetails;
        List<Expenses> expensesList = parsedFile.stream().map(row ->
                        new Expenses.ExpenseBuilder(user)
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

    @Override
    public List<Expenses> previewExpenses(File file, StatementPreviewRequest request) throws IOException, TrackerBadRequestException {
        String extension = file.getName().split("\\.")[1];
        FileProcessor fileProcessor = FileProcessorFactory.createFileProcessor(extension);
        if (fileProcessor == null){
            throw new BadRequestException("File format is invalid. Please upload xlsx files only");
        }
        List<HashMap<Integer, String>> parsedFile;

        try{
            parsedFile = fileProcessor.parseFile(file);
        }catch (TrackerBadRequestException ex){
            LOGGER.error("unable to parse the file : {}", ex.getMessage());
            throw ex;
        }
        validatePreviewInputs(parsedFile, request);
        return parsedFile.stream()
                .limit(5)
                .map(row ->
                        new Expenses.ExpenseBuilder(null)
                            .onDate(
                                    LocalDate.parse(
                                            String.valueOf(row.get(request.getTransactionDate())),
                                            formatter)
                            )
                            .withDescription(row.get(request.getDescription()))
                            .withBankReferenceNo(row.get(request.getBankReferenceNo()))
                            .setDebit(row.get(request.getDebit()) != null ? Double.parseDouble(row.get(request.getDebit())) : 0.0)
                            .setCredit(row.get(request.getCredit()) != null ? Double.parseDouble(row.get(request.getCredit())) : 0.0)
                            .setClosingBalance(row.get(request.getClosingBalance()) != null ? Double.parseDouble(row.get(request.getClosingBalance())) : 0.0)
                            .build()
                ).toList();
    }

    private void validatePreviewInputs(List<HashMap<Integer, String>> parsedFile, StatementPreviewRequest request) {
         HashMap<Integer, String> row = parsedFile.getFirst();
         try{
             LocalDate.parse(String.valueOf(row.get(request.getTransactionDate())), formatter);
         }catch (DateTimeParseException ex){
             throw new TrackerBadRequestException("transactionDate cannot be parsed");
         }
         try{
             if (row.get(request.getDebit()) != null) {
                 Double.parseDouble(row.get(request.getDebit()));
             }
         }catch (NumberFormatException ex){
             throw new TrackerBadRequestException("Debit cannot be parsed");
         }
         try{
             if (row.get(request.getCredit()) != null){
                 Double.parseDouble(row.get(request.getCredit()));
             }
         }catch (NumberFormatException ex){
             throw new TrackerBadRequestException("Credit cannot be parsed");
         }
        try{
            if (row.get(request.getClosingBalance()) != null){
                Double.parseDouble(row.get(request.getClosingBalance()));
            }
        }catch (NumberFormatException ex){
            throw new TrackerBadRequestException("ClosingBalance cannot be parsed");
        }
    }
}
