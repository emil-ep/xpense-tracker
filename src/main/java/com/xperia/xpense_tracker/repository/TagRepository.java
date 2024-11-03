package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, String> {

    Tag findByName(String name);
}
