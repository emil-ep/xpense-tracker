package com.xperia.xpense_tracker.models.entities;

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

    @Enumerated(EnumType.STRING)
    private TagType tagType;

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
    @JoinColumn(name = "category_id", nullable = false)
    private TagCategory category;

    public Tag(String name, Tag parentTag, TagType tagType, TrackerUser user, String[] keywords,
               boolean canBeConsideredExpense, TagCategory category){
        this.name = name;
        this.tagType = tagType;
        this.user = user;
        this.keywords = keywords;
        this.canBeConsideredExpense = canBeConsideredExpense;
        this.category = category;
    }

    public boolean isEditable(){
        return false;
    }
}
