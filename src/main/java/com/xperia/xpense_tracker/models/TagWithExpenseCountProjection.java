package com.xperia.xpense_tracker.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagWithExpenseCountProjection {

    private String id;

    private String name;

    private String[] keywords;

    private String color;

    private String categoryId;

    private String categoryName;

    private Long expenseCount;

    public TagWithExpenseCountProjection(String id, String name, String[] keywords, String color,  String categoryId, String categoryName, Long expenseCount) {
        this.id = id;
        this.name = name;
        this.keywords = keywords;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.color = color;
        this.expenseCount = expenseCount;
    }
}
