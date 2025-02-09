package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.Tag;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, String> {

    Optional<Tag> findByNameAndUser(String name, TrackerUser user);

    List<Tag> findAllByUser(TrackerUser user);

    Optional<Tag> findTagById(String id);
}
