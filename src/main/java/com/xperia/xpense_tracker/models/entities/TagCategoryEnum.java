package com.xperia.xpense_tracker.models.entities;

import lombok.Getter;

@Getter
public enum TagCategoryEnum {

    BANK_SAVINGS("Bank Saving", false),
    MUTUAL_FUND("Mutual Fund savings", false),
    SHOPPING("Shopping", true),
    PETS("Pets", true),
    RENT("Rent", true),
    LOAN("Loan", true),
    TRANSPORT("Transportation", true),
    GROCERY("Grocery", true),
    EDUCATION("Education", true),
    OTHER_EXPENSE("Other Expenses", true),
    OTHER_SAVINGS("Other Savings", false),
    LIFE_INSURANCE("Insurance", true),
    HEALTH_INSURANCE("Health Insurance", true),
    ONLINE_FOOD_DELIVERY("Online Food delivery", true),
    COMMUNICATION("Communication", true),
    DINE_OUT("Dine-Out", true),
    MEDICINE("Medicine", true),
    TRAVEL("Travelling", true),
    BEVERAGES("Bevarage", true),
    SALARY("Salary", false);

    private final String name;

    private final Boolean isExpense;

    TagCategoryEnum(String name, boolean isExpense){
        this.name = name;
        this.isExpense = isExpense;
    }




}
