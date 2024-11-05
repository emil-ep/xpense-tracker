package com.xperia.xpense_tracker.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "tag")
@NoArgsConstructor
@Getter
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
    private TrackerUser user;

    @ManyToMany(mappedBy = "tags")
    @JsonBackReference
    private Set<Expenses> expenses = new HashSet<>();

    private String[] keywords;

    public boolean isEditable(){
        return false;
    }
}
