package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.Tag;
import com.xperia.xpense_tracker.models.entities.TagType;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, String> {

    Tag findByName(String name);

    List<Tag> findAllByUser(TrackerUser user);

    List<Tag> findByTagType(TagType tagType);
}
