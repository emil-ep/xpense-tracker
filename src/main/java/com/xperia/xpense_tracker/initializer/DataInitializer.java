package com.xperia.xpense_tracker.initializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xperia.xpense_tracker.models.entities.tracker.*;
import com.xperia.xpense_tracker.models.settings.BankAccountType;
import com.xperia.xpense_tracker.models.settings.SettingsType;
import com.xperia.xpense_tracker.models.settings.UserSettingsFactory;
import com.xperia.xpense_tracker.repository.tracker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TagCategoryRepository tagCategoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingRepository userSettingRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ExpensesRepository expensesRepository;
    @Autowired
    private RemovedExpensesRepository removedExpensesRepository;
    @Autowired
    private UserBankAccountRepository userBankAccountRepository;

    @Override
    public void run(String... args) throws Exception {

        List<TagCategory> tagCategories =  tagCategoryRepository.findAll();
        if (tagCategories.isEmpty() || tagCategories.size() != TagCategoryEnum.values().length){

            Set<String> existingTagCategoryNames = tagCategories.stream().map(TagCategory::getName).collect(Collectors.toSet());
            LOGGER.debug("Tag categories are empty or new tag categories are added. Populating from list...");
            List<TagCategory> list = Arrays.stream(TagCategoryEnum.values())
                    .filter(tagCategoryEnum -> !existingTagCategoryNames.contains(tagCategoryEnum.getName()))
                    .map(tagCategoryEnum -> new TagCategory(tagCategoryEnum.getName(), tagCategoryEnum.getIsExpense()))
                    .toList();
            LOGGER.debug("{} tag categories are added", list.size());
            tagCategoryRepository.saveAll(list);
            LOGGER.debug("Tag categories populated");
        }
        //For settings to be initialised
        List<TrackerUser> availableUsers = userRepository.findAll();
        availableUsers.stream().forEach(user -> {
            Arrays.stream(SettingsType.values()).forEach(type -> {
                Optional<UserSettings> userSetting = userSettingRepository.findByUserAndType(user, type);
                if (userSetting.isEmpty()){
                    UserSettings newUserSetting = new UserSettings(type, user, UserSettingsFactory.createUserSettings(type));
                    userSettingRepository.save(newUserSetting);
                }
            });
        });
        //initialise bank accounts for users
        // Generate a default bank accounts and then attach it to the tags and expenses
        for (TrackerUser user: availableUsers){
            List<UserBankAccount> bankAccountsOfUser = user.getBankAccounts();
            if (bankAccountsOfUser != null && !bankAccountsOfUser.isEmpty()){
                return;
            }

            UserBankAccount defaultAccount = new UserBankAccount("Default", BankAccountType.NONE, user);
            userBankAccountRepository.save(defaultAccount);

            List<Expenses> expensesOfUser = expensesRepository.getExpensesByUser(user);
            expensesOfUser.forEach(expense -> expense.setBankAccount(defaultAccount));
            expensesRepository.saveAll(expensesOfUser);

            List<Tag> tagsOfUser = tagRepository.findAllByUser(user);
            tagsOfUser.forEach(tag -> tag.setBankAccount(defaultAccount));
            tagRepository.saveAll(tagsOfUser);

            List<RemovedExpense> removedExpenses = removedExpensesRepository.findAllByUser(user);
            removedExpenses.forEach(removedExpense -> removedExpense.setBankAccount(defaultAccount));
            removedExpensesRepository.saveAll(removedExpenses);
        }
    }
}
