package com.xperia.xpense_tracker.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TagRequest {

    private String id;

    private String name;

    private String parentTagId;

    private String[] keywords;

    private String tagCategoryId;

    private boolean canBeCountedAsExpense;

}
