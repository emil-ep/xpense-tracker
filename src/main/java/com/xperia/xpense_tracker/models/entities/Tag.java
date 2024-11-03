package com.xperia.xpense_tracker.models.entities;

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

    private TagType tagType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private TrackerUser user;

    private boolean editable;

    @ManyToMany(mappedBy = "tags")
    private Set<Expenses> expenses = new HashSet<>();


    public boolean isEditable(){
        return tagType.equals(TagType.CUSTOM);
    }
}
