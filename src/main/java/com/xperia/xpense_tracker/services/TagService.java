package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.Tag;
import com.xperia.xpense_tracker.models.entities.TagCategory;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.request.TagRequest;
import com.xperia.xpense_tracker.models.request.TagsEditRequest;

import java.util.List;
import java.util.Set;

public interface TagService {

    List<Tag> findAllTagsForUser(TrackerUser user);

    Tag addNewTag(TagRequest tagRequest, TrackerUser user);

    Tag editTag(TagRequest tagRequest, TrackerUser user);

    List<Tag> findTagsByTagIds(Set<String> tagIds);

    List<Tag> editTags(TagsEditRequest tagsRequest, TrackerUser user);

    void deleteTag(String tagId, TrackerUser user);

    List<TagCategory> fetchTagCategories();
}
