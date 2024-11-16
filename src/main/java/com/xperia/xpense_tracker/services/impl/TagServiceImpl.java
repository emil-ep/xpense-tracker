package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerException;
import com.xperia.xpense_tracker.models.entities.Tag;
import com.xperia.xpense_tracker.models.entities.TagType;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.request.TagRequest;
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

    @Override
    public Tag addNewTag(TagRequest tagRequest, TrackerUser user) throws TrackerException{
        Tag parentTag = null;
        if(tagRequest.getParentTagId() != null){
            if(tagRepository.findTagById(tagRequest.getParentTagId()).isEmpty()){
                throw new TrackerBadRequestException("Parent tagId is not valid");
            }
            parentTag = tagRepository.findTagById(tagRequest.getParentTagId()).get();
        }
        if(tagRequest.getKeywords() == null || tagRequest.getKeywords().length == 0){
            throw new TrackerBadRequestException("Keywords cannot be empty");
        }
        if (tagRequest.getName() == null || tagRequest.getName().isEmpty()){
            throw new TrackerBadRequestException("Name cannot be empty");
        }
        Tag tag = new Tag(
                tagRequest.getName(),
                parentTag,
                TagType.CUSTOM,
                user,
                tagRequest.getKeywords(),
                tagRequest.isCanBeCountedAsExpense()
        );
        return tagRepository.save(tag);
    }

    @Override
    public Tag editTag(TagRequest tagRequest, TrackerUser user) {
        Tag parentTag = null;
        if (tagRequest.getId() == null || tagRequest.getId().isEmpty()){
            throw new TrackerBadRequestException("id should not empty");
        }
        if (tagRequest.getParentTagId() != null){
            if(tagRepository.findTagById(tagRequest.getParentTagId()).isEmpty()){
                throw new TrackerBadRequestException("Parent tag is not valid");
            }
            parentTag = tagRepository.findTagById(tagRequest.getParentTagId())
                    .orElseThrow(() -> new TrackerBadRequestException("Parent tag not valid"));
        }
        Tag existingTag = tagRepository.findTagById(tagRequest.getId())
                .orElseThrow(() -> new TrackerBadRequestException("tagId not valid"));
        existingTag.setName(tagRequest.getName());
        existingTag.setParentTag(parentTag);
        existingTag.setKeywords(tagRequest.getKeywords());
        existingTag.setCanBeConsideredExpense(tagRequest.isCanBeCountedAsExpense());
        return tagRepository.save(existingTag);
    }
}
