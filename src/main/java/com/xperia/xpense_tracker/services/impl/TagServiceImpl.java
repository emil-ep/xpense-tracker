package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerException;
import com.xperia.xpense_tracker.models.entities.Tag;
import com.xperia.xpense_tracker.models.entities.TagCategory;
import com.xperia.xpense_tracker.models.entities.TagType;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.request.TagRequest;
import com.xperia.xpense_tracker.models.request.TagsEditRequest;
import com.xperia.xpense_tracker.repository.TagCategoryRepository;
import com.xperia.xpense_tracker.repository.TagRepository;
import com.xperia.xpense_tracker.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagCategoryRepository tagCategoryRepository;


    @Override
    public List<Tag> findAllTagsForUser(TrackerUser user) {
        List<Tag> userTags = tagRepository.findAllByUser(user);
        return userTags;
    }

    @Override
    public Tag addNewTag(TagRequest tagRequest, TrackerUser user) throws TrackerException{
        Tag parentTag = null;
        Optional<TagCategory> tagCategory = Optional.empty();
        if(tagRequest.getParentTagId() != null){
            if(tagRepository.findTagById(tagRequest.getParentTagId()).isEmpty()){
                throw new TrackerBadRequestException("Parent tagId is not valid");
            }
            parentTag = tagRepository.findTagById(tagRequest.getParentTagId()).get();
        }
        Optional<Tag> existingTag = tagRepository.findByNameAndUser(tagRequest.getName(), user);
        if (existingTag.isPresent()){
            throw new TrackerBadRequestException("Tag with same name exists");
        }
        if(tagRequest.getKeywords() == null || tagRequest.getKeywords().length == 0){
            throw new TrackerBadRequestException("Keywords cannot be empty");
        }
        if (tagRequest.getTagCategoryId() == null || tagRequest.getTagCategoryId().isEmpty()){
            throw new TrackerBadRequestException("Tag category is not valid");
        }
        if (tagRequest.getName() == null || tagRequest.getName().isEmpty()){
            throw new TrackerBadRequestException("Name cannot be empty");
        }
        tagCategory = tagCategoryRepository.findById(tagRequest.getTagCategoryId());
        if (tagCategory.isEmpty()){
            throw new TrackerBadRequestException("Tag category id is not valid");
        }

        Tag tag = new Tag(
                tagRequest.getName(),
                parentTag,
                user,
                tagRequest.getKeywords(),
                tagRequest.isCanBeCountedAsExpense(),
                tagCategory.get(),
                tagRequest.getColor()
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

    @Override
    public List<Tag> findTagsByTagIds(Set<String> tagIds) {
        return tagRepository.findAllById(tagIds);
    }

    @Override
    public List<Tag> editTags(TagsEditRequest tagsRequest, TrackerUser user) {

        List<Tag> existingTags = tagRepository.findAllByUser(user);
        List<Tag> modifiedTags = new ArrayList<>();
        tagsRequest.getTags().forEach(tagRequest -> {
            Tag tagToBeUpdated = existingTags
                    .stream()
                    .filter(tag -> tagRequest.getId().equals(tag.getId()))
                    .findFirst()
                    .orElseThrow(
                            () -> new TrackerBadRequestException("Tag id " + tagRequest.getId() + " not valid")
                    );
            TagCategory category = tagCategoryRepository.findById(tagRequest.getCategory().getId())
                    .orElseThrow(() -> new TrackerBadRequestException("Tag category id provided for tag " + tagRequest.getName() + "is not correct"));
            tagToBeUpdated.setName(tagRequest.getName());
            tagToBeUpdated.setKeywords(tagRequest.getKeywords().split(","));
            tagToBeUpdated.setCanBeConsideredExpense(tagRequest.isCanBeCountedAsExpense());
            tagToBeUpdated.setCategory(category);
            modifiedTags.add(tagToBeUpdated);
        });
        return tagRepository.saveAll(modifiedTags);
    }

    @Override
    public void deleteTag(String tagId, TrackerUser user) {
        List<Tag> tagsByUser = tagRepository.findAllByUser(user);
        if(tagsByUser.stream().noneMatch(tag -> tagId.equals(tag.getId()))){
            throw new TrackerBadRequestException("Tag Id is not valid");
        }

        tagRepository.deleteById(tagId);
    }

    @Override
    public List<TagCategory> fetchTagCategories() {
        List<TagCategory> categories = tagCategoryRepository.findAll();
        return categories;
    }
}
