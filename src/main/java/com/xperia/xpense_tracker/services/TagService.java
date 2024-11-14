package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.Tag;
import com.xperia.xpense_tracker.models.entities.TrackerUser;

import java.util.List;

public interface TagService {

    List<Tag> findAllTagsForUser(TrackerUser user);
}
