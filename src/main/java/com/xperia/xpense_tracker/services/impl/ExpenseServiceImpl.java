package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.DescriptionNormaliser;
import com.xperia.xpense_tracker.cache.CacheService;
import com.xperia.xpense_tracker.models.ParsedRowData;
import com.xperia.xpense_tracker.models.entities.tracker.*;
import com.xperia.xpense_tracker.models.fileProcessors.FileProcessor;
import com.xperia.xpense_tracker.models.fileProcessors.FileProcessorFactory;
import com.xperia.xpense_tracker.models.request.StatementPreviewRequest;
import com.xperia.xpense_tracker.models.request.UpdateExpenseRequest;
import com.xperia.xpense_tracker.models.response.MonthlyDebitSummary;
import com.xperia.xpense_tracker.repository.tracker.ExpensesRepository;
import com.xperia.xpense_tracker.repository.tracker.RemovedExpensesRepository;
import com.xperia.xpense_tracker.services.*;
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
import org.xperia.exception.TrackerBadRequestException;
import org.xperia.exception.TrackerNotFoundException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.xperia.xpense_tracker.cache.CacheNames.METRICS_CACHE_NAME;

@Service
public class ExpenseServiceImpl implements ExpenseService {


    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final DateTimeFormatter formatter_1 = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter formatter_2 = DateTimeFormatter.ofPattern("d MMM yyyy");
    private static final DateTimeFormatter formatter_3 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private SyncStatusService syncStatusService;

    @Autowired
    private RemovedExpensesRepository removedExpensesRepository;

    @Autowired
    private RemovedExpensesService removedExpensesService;

    @Autowired
    private StatementService statementService;

    @Autowired
    private UserBankAccountService userBankAccountService;

    @Autowired
    private CacheService cache;


    @Override
    public Page<Expenses> getExpenses(UserDetails userDetails, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {

        TrackerUser user = (TrackerUser) userDetails;
        return expensesRepository.getPaginatedExpensesByUser(user, startDate, endDate, pageRequest);
    }

    @Override
    public List<Expenses> processExpenseFromFile(File file, StatementPreviewRequest request, UserDetails userDetails,
                                                 boolean isPreview) throws IOException {
        String extension = file.getName().split("\\.")[1];
        TrackerUser user = (TrackerUser) userDetails;
        Optional<UserBankAccount> bankAccount = userBankAccountService.findBankAccount(request.getBankAccountId(), user);
        if (bankAccount.isEmpty()){
            throw new TrackerBadRequestException("Unknown bank account provided");
        }
        Optional<Statements> statement = statementService.findByFileName(file.getName());
        FileProcessor fileProcessor = FileProcessorFactory.createFileProcessor(extension.toLowerCase());
        if (fileProcessor == null) {
            throw new BadRequestException("File format is invalid. Please upload xlsx files only");
        }
        List<HashMap<Integer, String>> parsedFile;
        try {
            parsedFile = fileProcessor.parseFile(file, request.getHeaderStartIndex());
        } catch (TrackerBadRequestException ex) {
            LOGGER.error("unable to parse the file : {}", ex.getMessage(), ex);
            throw ex;
        }
        //Here we return the row index and the compatible date time formatter for valid data
        ParsedRowData parsedRowData = findCompatibleDateFormatter(parsedFile, request.getTransactionDate());
        validatePreviewInputs(parsedFile, request, parsedRowData);
        List<Tag> userTags = tagService.findAllTagsForUser(user);
        List<Expenses> expensesList = new ArrayList<>();
        parsedFile.stream()
            .limit(isPreview ? 5 : parsedFile.size()) //limit processing to 5 elements if it's a preview
            .filter(row -> !removedExpensesService.isRemovedExpense(row.get(request.getDescription()),
                    row.get(request.getBankReferenceNo())))
            .forEach(row -> {
                //There are situations where there is invalid entries (like row separator or end of file details,
                //In this case, we need to skip the line
                try{
                    LocalDate.parse(String.valueOf(row.get(request.getTransactionDate())), parsedRowData.getDateTimeFormatter());
                }catch (DateTimeParseException ex){
                    LOGGER.debug("Unable to parse date from this row. Skipping this line ..");
                    return;
                }
                String transactionDescription = row.get(request.getDescription());
                Set<Tag> matchedTags = findMatchingTags(userTags, transactionDescription);
                Expenses expense = new Expenses.ExpenseBuilder(user, bankAccount.get())
                        .onDate(LocalDate.parse(String.valueOf(row.get(request.getTransactionDate())), parsedRowData.getDateTimeFormatter()))
                        .withDescription(transactionDescription)
                        .withBankReferenceNo(row.get(request.getBankReferenceNo()))
                        .setDebit(
                                row.get(request.getDebit()) != null && !row.get(request.getDebit()).isEmpty()
                                        ? Double.parseDouble(cleanAmountValue(row.get(request.getDebit())))
                                        : 0.0
                        )
                        .setCredit(
                                row.get(request.getCredit()) != null && !row.get(request.getCredit()).isEmpty()
                                        ? Double.parseDouble(cleanAmountValue(row.get(request.getCredit())))
                                        : 0.0
                        )
                        .setClosingBalance(row.get(request.getClosingBalance()) != null
                                ? Double.parseDouble(cleanAmountValue(row.get(request.getClosingBalance())))
                                : 0.0)
                        .withTags(matchedTags)
                        .ofStatement(statement.orElse(null))
                        .build();
                expensesList.add(expense);
            });
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
                .withAttachment(expenseRequest.getAttachment() != null
                        ? expenseRequest.getAttachment()
                        : existingExpense.getAttachment())
                .withNote(expenseRequest.getNotes() != null
                        ? expenseRequest.getNotes()
                        : existingExpense.getNotes())
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
            SyncStatus inProgressStatus = new SyncStatus(requestId, SyncStatusEnum.IN_PROGRESS);
            syncStatusService.saveStatus(inProgressStatus);
            Long startTime = System.currentTimeMillis();
            LOGGER.info("--- Started syncing expenses v1 ---");
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
            Long endTime = System.currentTimeMillis();
            LOGGER.info("--- Completed syncing in {} seconds ----", (endTime - startTime)/1000);
            expensesRepository.saveAll(updatesExpenses);
            SyncStatus completedStatus = new SyncStatus(requestId, SyncStatusEnum.COMPLETED);
            syncStatusService.updateStatus(completedStatus);
        }catch (Exception ex){
            SyncStatus failedStatus = new SyncStatus(requestId, SyncStatusEnum.FAILED);
            syncStatusService.updateStatus(failedStatus);
            LOGGER.error("Failure while syncing expenses for user - requestId : {} : ex : {} ", requestId, ex.getMessage(), ex);
        }finally {
            SyncStatus completedStatus = new SyncStatus(requestId, SyncStatusEnum.COMPLETED);
            syncStatusService.updateStatus(completedStatus);
            LOGGER.info("completed sync request: {}", requestId);
        }
    }

    /**
     * This algorithm needs to be further refined. Currently under investigation.
     * Did not see marginal difference in synchorinising time. This should not be called by any chance,
     * also the save method is commented out to prevent saving this to database
     * @param userDetails The user who is triggering the sync functionality
     * @param requestId the requestId generated for tracking purposes
     */
    @Override
    public void syncExpensesV2(UserDetails userDetails, String requestId) {
        TrackerUser user = (TrackerUser) userDetails;
        try{
            SyncStatus inProgressStatus = new SyncStatus(requestId, SyncStatusEnum.IN_PROGRESS);
            syncStatusService.saveStatus(inProgressStatus);
            long startTime = System.currentTimeMillis();
            LOGGER.info("--- Started syncing expenses v2 ---");
            List<Expenses> userExpenses = expensesRepository.getExpensesByUser(user);
            List<Tag> userTags = tagService.findAllTagsForUser(user);
            Map<String, Set<Tag>> keywordTagMap = new HashMap<>();
            for (Tag tag: userTags){
                for (String keyword: tag.getKeywords()){
                    if (!keywordTagMap.containsKey(keyword)){
                        keywordTagMap.computeIfAbsent(keyword, k -> new HashSet<>()).add(tag);
                    }else {
                        keywordTagMap.computeIfPresent(keyword, (k, tags) -> {
                            tags.add(tag);
                            return tags;
                        });
                    }
                }
            }
            List<Expenses> expenseList = new ArrayList<>();
            for (Expenses expenses : userExpenses){
                String normalised = DescriptionNormaliser.normalize(expenses.getDescription());
                List<String> tokens = tokenize(normalised);
                Set<Tag> matchedTags = new HashSet<>();
                for (String token: tokens){
                    if (keywordTagMap.containsKey(token)){
                        matchedTags.addAll(keywordTagMap.get(token));
                    }
                }
                Expenses newExpense = new Expenses.ExpenseBuilder(expenses)
                        .withTags(matchedTags)
                        .build(expenses.getId());
                expenseList.add(newExpense);
            }
            LOGGER.info("--- Completed syncing v2 in {} seconds ----", (System.currentTimeMillis() - startTime)/1000);
//            expensesRepository.saveAll(expenseList);

        }catch (Exception ex){
            SyncStatus failedStatus = new SyncStatus(requestId, SyncStatusEnum.FAILED);
            syncStatusService.updateStatus(failedStatus);
            LOGGER.error("Failure while syncing expenses for user - requestId : {} : ex : {} ", requestId, ex.getMessage(), ex);
        }finally {
            SyncStatus completedStatus = new SyncStatus(requestId, SyncStatusEnum.COMPLETED);
            syncStatusService.updateStatus(completedStatus);
            LOGGER.info("completed sync request: {}", requestId);
        }
    }

    public static List<String> tokenize(String normalized) {
        return Arrays.asList(normalized.split(" "));
    }

    @Override
    public List<Expenses> listAll() {
        return expensesRepository.findAll();
    }

    @Override
    public void softDeleteExpense(String id, UserDetails userDetails) {
        Optional<Expenses> expense = expensesRepository.findExpensesById(id);
        if (expense.isEmpty()){
            throw new TrackerNotFoundException("Expense with id : " + id + " not found");
        }

        RemovedExpense removedExpense = new RemovedExpense(expense.get());
        //Removing foreign key constraint
        expense.get().getTags().clear();
        expensesRepository.save(expense.get());
        // done
        expensesRepository.deleteById(id);
        cache.clearCache(METRICS_CACHE_NAME, ((TrackerUser) userDetails).getId());
        removedExpensesRepository.save(removedExpense);
    }

    @Override
    public Map<String, String> matchHeaders(List<String> headers) {
        Map<String, String> possibleMatches = new HashMap<>();
        for (String header: headers){
            ExpenseFields matchingField = ExpenseFields.findMatchingField(header);
            if (matchingField != null){
                possibleMatches.put(matchingField.getFieldName(), header);
            }
        }
        return possibleMatches;
    }

    @Override
    public List<Statements> findDistinctStatementsOfExpenses() {
        return expensesRepository.findDistinctStatements();
    }

    private String generateIdentifier(LocalDate date, String bankReferenceNo, String userId) {
        return date.toString() + "_" + bankReferenceNo + "_" + userId;
    }

    private String cleanAmountValue(String value){
        String cleanedValue = value.replaceAll("[^0-9.]", "");
        if (cleanedValue.isEmpty() || cleanedValue.isBlank()){
            return "0.0";
        }
        return cleanedValue;
    }

    private ParsedRowData findCompatibleDateFormatter(List<HashMap<Integer, String>> parsedFile, Integer dateIndex){
        int rowIndex = 0;
        for (HashMap<Integer, String> row: parsedFile){
            try {
                LocalDate.parse(String.valueOf(row.get(dateIndex)), formatter);
                return new ParsedRowData(rowIndex, formatter);
            } catch (DateTimeParseException ex) {
                LOGGER.error("Unable to parse transactionDate in the format dd/MM/yy");
            }
            try{
                LocalDate.parse(String.valueOf(row.get(dateIndex)), formatter_1);
                return new ParsedRowData(rowIndex, formatter_1);
            }catch (DateTimeParseException ex){
                LOGGER.error("Unable to parse transactionDate in the format dd MMM yyyy");
            }

            try{
                LocalDate.parse(String.valueOf(row.get(dateIndex)), formatter_2);
                return new ParsedRowData(rowIndex, formatter_2);
            }catch (DateTimeParseException ex){
                LOGGER.error("Unable to parse transactionDate in the format d MMM yyyy");
            }

            try{
                LocalDate.parse(String.valueOf(row.get(dateIndex)), formatter_3);
                return new ParsedRowData(rowIndex, formatter_3);
            }catch (DateTimeParseException ex){
                LOGGER.error("Unable to parse transactionDate in the format dd/MM/yyyy");
            }
            rowIndex ++;
        }
        throw new TrackerBadRequestException("Unable to parse transaction date");
    }

    private void validatePreviewInputs(List<HashMap<Integer, String>> parsedFile, StatementPreviewRequest request,
                                       ParsedRowData parsedRowData) {
        HashMap<Integer, String> row = parsedFile.get(parsedRowData.getParsedRowIndex());

        try {
            LocalDate.parse(String.valueOf(row.get(request.getTransactionDate())), parsedRowData.getDateTimeFormatter());
        } catch (DateTimeParseException ex) {
            throw new TrackerBadRequestException("unable to parse transactionDate");
        }
        try {
            if (row.get(request.getDebit()) != null) {
                if (row.get(request.getDebit()) != null && !row.get(request.getDebit()).trim().isEmpty())
                    Double.parseDouble(cleanAmountValue(row.get(request.getDebit())));
            }
        } catch (NumberFormatException ex) {
            throw new TrackerBadRequestException("Debit cannot be parsed");
        }
        try {
            if (row.get(request.getCredit()) != null) {
                if (row.get(request.getCredit()) != null && !row.get(request.getCredit()).trim().isEmpty())
                    Double.parseDouble(cleanAmountValue(row.get(request.getCredit())));
            }
        } catch (NumberFormatException ex) {
            throw new TrackerBadRequestException("Credit cannot be parsed");
        }
        try {
            if (row.get(request.getClosingBalance()) != null) {
                if (row.get(request.getClosingBalance()) != null && !row.get(request.getClosingBalance()).trim().isEmpty())
                    Double.parseDouble(cleanAmountValue(row.get(request.getClosingBalance())));
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
