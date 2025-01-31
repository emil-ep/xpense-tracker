package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.TagCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagCategoryRepository extends JpaRepository<TagCategory, String> {


}
