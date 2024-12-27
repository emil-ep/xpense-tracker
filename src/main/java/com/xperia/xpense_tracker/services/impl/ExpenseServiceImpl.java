package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.entities.Tag;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.fileProcessors.FileProcessor;
import com.xperia.xpense_tracker.models.fileProcessors.FileProcessorFactory;
import com.xperia.xpense_tracker.models.request.StatementPreviewRequest;
import com.xperia.xpense_tracker.models.request.UpdateExpenseRequest;
import com.xperia.xpense_tracker.models.response.MonthlyDebitSummary;
import com.xperia.xpense_tracker.repository.ExpensesRepository;
import com.xperia.xpense_tracker.services.ExpenseService;
import com.xperia.xpense_tracker.services.TagService;
import jakarta.persistence.Tuple;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {


    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final DateTimeFormatter formatter_1 = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter formatter_2 = DateTimeFormatter.ofPattern("d MMM yyyy");

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private TagService tagService;

    @Override
    public Page<Expenses> getExpenses(UserDetails userDetails, PageRequest pageRequest) {

        TrackerUser user = (TrackerUser) userDetails;
        return expensesRepository.getPaginatedExpensesByUser(user, pageRequest);
    }

    @Override
    public List<Expenses> processExpenseFromFile(File file, StatementPreviewRequest request, UserDetails userDetails, boolean isPreview) throws IOException {
        String extension = file.getName().split("\\.")[1];
        FileProcessor fileProcessor = FileProcessorFactory.createFileProcessor(extension);
        if (fileProcessor == null) {
            throw new BadRequestException("File format is invalid. Please upload xlsx files only");
        }
        List<HashMap<Integer, String>> parsedFile;
        try {
            parsedFile = fileProcessor.parseFile(file);
        } catch (TrackerBadRequestException ex) {
            LOGGER.error("unable to parse the file : {}", ex.getMessage());
            throw ex;
        }
        DateTimeFormatter compatibleDateFormatter = findCompatibleDateFormatter(parsedFile, request.getTransactionDate());
        validatePreviewInputs(parsedFile, request, compatibleDateFormatter);
        TrackerUser user = (TrackerUser) userDetails;
        List<Tag> userTags = tagService.findAllTagsForUser(user);
        List<Expenses> expensesList = parsedFile.stream()
                .limit(isPreview ? 5 : parsedFile.size()) //limit processing to 5 elements if it's a preview
                .map(row -> {
                    String transactionDescription = row.get(request.getDescription());
                    Set<Tag> matchedTags = findMatchingTags(userTags, transactionDescription);
                    return new Expenses.ExpenseBuilder(user)
                            .onDate(LocalDate.parse(String.valueOf(row.get(request.getTransactionDate())), compatibleDateFormatter))
                            .withDescription(transactionDescription)
                            .withBankReferenceNo(row.get(request.getBankReferenceNo()))
                            .setDebit(row.get(request.getDebit()) != null ? Double.parseDouble(row.get(request.getDebit())) : 0.0)
                            .setCredit(row.get(request.getCredit()) != null ? Double.parseDouble(row.get(request.getCredit())) : 0.0)
                            .setClosingBalance(row.get(request.getClosingBalance()) != null ? Double.parseDouble(row.get(request.getClosingBalance())) : 0.0)
                            .withTags(matchedTags)
                            .build();
                        }
                ).toList();
        LOGGER.debug("Total expenses found from the parsed file : {}", expensesList.size());
        if (isPreview) {
            return expensesList;
        }

        //the real duplicate checking starts from here
        //finding any existing expenses in database based on userId and transactionDates, bankReferenceNo from the parsedFile
        List<Expenses> existingExpenses = expensesRepository.findByUserAndTransactionDateInAndBankReferenceNoIn(
                user,
                expensesList.stream().map(Expenses::getTransactionDate).collect(Collectors.toSet()),
                expensesList.stream().map(Expenses::getBankReferenceNo).collect(Collectors.toSet()));

        LOGGER.debug("Expenses with same transactionDate and bankReferenceNo : {}", existingExpenses.size());
        //Generating unique items for existing expenses if any
        Set<String> existingExpenseIdentifiers = existingExpenses.stream()
                .map(e -> generateIdentifier(e.getTransactionDate(), e.getBankReferenceNo(), user.getId()))
                .collect(Collectors.toSet());

        //Filtering the new expenses and omitting any existing expense based on the generated identifier
        List<Expenses> expensesToSave = expensesList.stream()
                .filter(e -> !existingExpenseIdentifiers.contains(
                        generateIdentifier(e.getTransactionDate(), e.getBankReferenceNo(), user.getId())
                ))
                .toList();
        LOGGER.debug("Expenses to save : {}", expensesToSave.size());
        if (!expensesToSave.isEmpty()) {
            expensesRepository.saveAll(expensesToSave);
        }
        return expensesToSave;
    }

    @Override
    public boolean isValidExpenseOfUser(UserDetails userDetails, String expenseId) {
        TrackerUser user = (TrackerUser) userDetails;
        List<Expenses> expensesByUser = expensesRepository.getExpensesByUser(user);
        return expensesByUser.stream().anyMatch(expenses -> expenses.getId().equals(expenseId));
    }

    @Override
    public Expenses updateExpense(String expenseId, UpdateExpenseRequest expenseRequest, UserDetails userDetails) {
        if(!this.isValidExpenseOfUser(userDetails, expenseId)){
            throw new TrackerBadRequestException("Expense Id is not valid");
        }
        Expenses existingExpense = expensesRepository.findExpensesById(expenseId)
                .orElseThrow(() -> new TrackerBadRequestException("Expense Id is not valid"));

        Set<Tag> tags = null;
        if(expenseRequest.getTagIds() != null){
            tags = new HashSet<>(tagService.findTagsByTagIds(expenseRequest.getTagIds()));
        }

        Expenses expenseToUpdate = new Expenses.ExpenseBuilder(existingExpense)
                .withDescription(expenseRequest.getDescription() != null
                        ? expenseRequest.getDescription()
                        : existingExpense.getDescription())
                .withTags(expenseRequest.getTagIds() != null
                        ? tags
                        : existingExpense.getTags())
                .withBankReferenceNo(expenseRequest.getBankReferenceNo() != null
                        ? expenseRequest.getBankReferenceNo()
                        : existingExpense.getBankReferenceNo())
                .onDate(expenseRequest.getTransactionDate() != null
                        ? expenseRequest.getTransactionDate()
                        : existingExpense.getTransactionDate())
                .build(existingExpense.getId());

        return expensesRepository.save(expenseToUpdate);
    }


    @Override
    public List<MonthlyDebitSummary> aggregateExpenses(String by, UserDetails userDetails) {
        List<Tuple> result = expensesRepository.findMonthlyDebitSummaries((TrackerUser) userDetails);
        return result.stream()
                .map(tuple -> new MonthlyDebitSummary(
                        ((Number) tuple.get(0)).intValue(),   // year
                        ((Number) tuple.get(1)).intValue(),   // month
                        ((Number) tuple.get(2)).doubleValue(), // totalDebit
                        ((Number) tuple.get(3)).doubleValue()
                ))
                .collect(Collectors.toList());
    }

    /**
     * The sync method helps in re-syncing the entire expenses of user with the new tags
     *
     * @param userDetails The user who is triggering the sync functionality
     * @param requestId the requestId generated for tracking purposes
     */
    @Async
    @Override
    public void syncExpenses(UserDetails userDetails, String requestId) {
        TrackerUser user = (TrackerUser) userDetails;
        try{
            List<Expenses> userExpenses = expensesRepository.getExpensesByUser(user);
            List<Tag> userTags = tagService.findAllTagsForUser(user);
            List<Expenses> updatesExpenses = userExpenses.stream()
                    .map(expenses -> {
                        Set<Tag> matchedTags = findMatchingTags(userTags, expenses.getDescription());
                        return new Expenses.ExpenseBuilder(expenses)
                                .withTags(matchedTags)
                                .build(expenses.getId());
                    })
                    .toList();
            expensesRepository.saveAll(updatesExpenses);
        }catch (Exception ex){
            LOGGER.error("Failure while syncing expenses for user - requestId : {} : ex : {} ", requestId, ex.getMessage());
        }finally {
            LOGGER.info("completed sync request: {}", requestId);
        }
    }

    private String generateIdentifier(LocalDate date, String bankReferenceNo, String userId) {
        return date.toString() + "_" + bankReferenceNo + "_" + userId;
    }

    private DateTimeFormatter findCompatibleDateFormatter(List<HashMap<Integer, String>> parsedFile, Integer dateIndex){
        HashMap<Integer, String> row = parsedFile.getFirst();
        try {
            LocalDate.parse(String.valueOf(row.get(dateIndex)), formatter);
            return formatter;
        } catch (DateTimeParseException ex) {
            LOGGER.error("Unable to parse transactionDate in the format dd/MM/yy");
        }
        try{
            LocalDate.parse(String.valueOf(row.get(dateIndex)), formatter_1);
            return formatter_1;
        }catch (DateTimeParseException ex){
            LOGGER.error("Unable to parse transactionDate in the format dd MMM yyyy");
        }

        try{
            LocalDate.parse(String.valueOf(row.get(dateIndex)), formatter_2);
            return formatter_2;
        }catch (DateTimeParseException ex){
            throw new TrackerBadRequestException("Unable to parse transaction date");
        }
    }

    private void validatePreviewInputs(List<HashMap<Integer, String>> parsedFile, StatementPreviewRequest request, DateTimeFormatter formatter) {
        HashMap<Integer, String> row = parsedFile.getFirst();

        try {
            LocalDate.parse(String.valueOf(row.get(request.getTransactionDate())), formatter);
        } catch (DateTimeParseException ex) {
            throw new TrackerBadRequestException("unable to parse transactionDate");
        }
        try {
            if (row.get(request.getDebit()) != null) {
                if (row.get(request.getDebit()) != null && !row.get(request.getDebit()).trim().isEmpty())
                    Double.parseDouble(row.get(request.getDebit()));
            }
        } catch (NumberFormatException ex) {
            throw new TrackerBadRequestException("Debit cannot be parsed");
        }
        try {
            if (row.get(request.getCredit()) != null) {
                if (row.get(request.getCredit()) != null && !row.get(request.getCredit()).trim().isEmpty())
                    Double.parseDouble(row.get(request.getCredit()));
            }
        } catch (NumberFormatException ex) {
            throw new TrackerBadRequestException("Credit cannot be parsed");
        }
        try {
            if (row.get(request.getClosingBalance()) != null) {
                if (row.get(request.getClosingBalance()) != null && !row.get(request.getClosingBalance()).trim().isEmpty())
                    Double.parseDouble(row.get(request.getClosingBalance()));
            }
        } catch (NumberFormatException ex) {
            throw new TrackerBadRequestException("ClosingBalance cannot be parsed");
        }
    }

    private Set<Tag> findMatchingTags(List<Tag> userTags, String transactionDescription){
        Set<Tag> matchedTags = new HashSet<>();
        for (Tag tag: userTags){
            for (String keyword: tag.getKeywords()){
                if(transactionDescription.toLowerCase().contains(keyword.toLowerCase())){
                    matchedTags.add(tag);
                }
            }
        }
        return matchedTags;
    }
}
