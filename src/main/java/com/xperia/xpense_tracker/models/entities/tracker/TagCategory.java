package com.xperia.xpense_tracker.models.entities.tracker;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "tag_category")
@NoArgsConstructor
@Getter
@Setter
public class TagCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private boolean expense;

    public TagCategory(String name, boolean expense){
        this.name = name;
        this.expense = expense;
    }
}
