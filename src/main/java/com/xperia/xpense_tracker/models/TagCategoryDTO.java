package com.xperia.xpense_tracker.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagCategoryDTO {

    private String id;

    private String name;

    public TagCategoryDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

}
