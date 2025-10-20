package com.xperia.xpense_tracker.models.entities.tracker;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "tag")
@NoArgsConstructor
@Getter
@Setter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "parent_tag_id")
    private Tag parentTag;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private TrackerUser user;

    @ManyToMany(mappedBy = "tags")
    @JsonBackReference
    private Set<Expenses> expenses = new HashSet<>();

    private String[] keywords;

    private boolean canBeConsideredExpense;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private TagCategory category;

    private String color;

    public Tag(String name, Tag parentTag, TrackerUser user, String[] keywords,
               boolean canBeConsideredExpense, TagCategory category, String color){
        this.name = name;
        this.user = user;
        this.keywords = keywords;
        this.canBeConsideredExpense = canBeConsideredExpense;
        this.category = category;
        this.color = color;
    }

    public boolean isEditable(){
        return false;
    }
}
