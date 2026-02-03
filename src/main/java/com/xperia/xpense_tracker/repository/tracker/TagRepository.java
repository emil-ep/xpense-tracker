package com.xperia.xpense_tracker.repository.tracker;

import com.xperia.xpense_tracker.models.TagWithExpenseCountProjection;
import com.xperia.xpense_tracker.models.entities.tracker.Tag;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, String> {

    Optional<Tag> findByNameAndUser(String name, TrackerUser user);

    List<Tag> findAllByUser(TrackerUser user);

    Optional<Tag> findTagById(String id);

    @Query(value = """
            SELECT t.id as id,
                t.name as name,
                t.keywords as keywords,
                t.color as color,
                c.id as categoryId,
                c.name as categoryName,
                COUNT(et.expense_id) as expenseCount
            FROM tag t
            LEFT JOIN expense_tags et ON t.id = et.tag_id
            JOIN tag_category c ON t.category_id = c.id
            WHERE t.user_id = :userId
            GROUP BY t.id, t.name,t.keywords, t.color, c.id, c.name
            """, nativeQuery = true)
    List<TagWithExpenseCountProjection> findTagsWithCount(@Param("userId") String userId);
}
