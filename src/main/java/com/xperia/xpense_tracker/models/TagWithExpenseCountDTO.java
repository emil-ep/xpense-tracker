package com.xperia.xpense_tracker.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagWithExpenseCountDTO {

    private String id;

    private String name;

    private String[] keywords;

    private TagCategoryDTO category;

    private String color;

    private Long expenseCount;

    public TagWithExpenseCountDTO(String id, String name, String[] keywords, TagCategoryDTO category, String color,
                                  Long expenseCount) {
        this.id = id;
        this.name = name;
        this.keywords = keywords;
        this.category = category;
        this.color = color;
        this.expenseCount = expenseCount;
    }
}
