package com.xperia.xpense_tracker.initializer;

import com.xperia.xpense_tracker.models.entities.TagCategory;
import com.xperia.xpense_tracker.models.entities.TagCategoryEnum;
import com.xperia.xpense_tracker.repository.TagCategoryRepository;
import com.xperia.xpense_tracker.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TagCategoryRepository tagCategoryRepository;

    @Autowired
    private TagRepository tagRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(String... args) throws Exception {

        List<TagCategory> tagCategories =  tagCategoryRepository.findAll();
        if (tagCategories.isEmpty() || tagCategories.size() != TagCategoryEnum.values().length){

            Set<String> existingTagCategoryNames = tagCategories.stream().map(TagCategory::getName).collect(Collectors.toSet());
            LOGGER.debug("Tag categories are empty or new tag categories are added. Populating from list...");
            List<TagCategory> list = Arrays.stream(TagCategoryEnum.values())
                    .filter(tagCategoryEnum -> !existingTagCategoryNames.contains(tagCategoryEnum.getName()))
                    .map(tagCategoryEnum -> new TagCategory(tagCategoryEnum.getName(), tagCategoryEnum.getIsExpense()))
                    .toList();
            LOGGER.debug("{} tag categories are added", list.size());
            tagCategoryRepository.saveAll(list);
            LOGGER.debug("Tag categories populated");
        }
    }
}
