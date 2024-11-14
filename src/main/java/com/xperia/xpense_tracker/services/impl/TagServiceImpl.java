package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.Tag;
import com.xperia.xpense_tracker.models.entities.TagType;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.repository.TagRepository;
import com.xperia.xpense_tracker.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;


    @Override
    public List<Tag> findAllTagsForUser(TrackerUser user) {
        List<Tag> systemTags = tagRepository.findByTagType(TagType.SYSTEM);
        List<Tag> userTags = tagRepository.findAllByUser(user);
        List<Tag> tags = new ArrayList<>();
        tags.addAll(systemTags);
        tags.addAll(userTags);
        return tags;
    }
}
