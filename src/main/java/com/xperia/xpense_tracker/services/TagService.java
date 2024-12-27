package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.Tag;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.request.TagRequest;

import java.util.List;
import java.util.Set;

public interface TagService {

    List<Tag> findAllTagsForUser(TrackerUser user);

    Tag addNewTag(TagRequest tagRequest, TrackerUser user);

    Tag editTag(TagRequest tagRequest, TrackerUser user);

    List<Tag> findTagsByTagIds(Set<String> tagIds);
}
