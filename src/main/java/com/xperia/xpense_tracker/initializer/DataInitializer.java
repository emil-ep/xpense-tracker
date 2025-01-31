package com.xperia.xpense_tracker.initializer;

import com.xperia.xpense_tracker.models.entities.TagCategory;
import com.xperia.xpense_tracker.models.entities.TagCategoryEnum;
import com.xperia.xpense_tracker.repository.TagCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TagCategoryRepository tagCategoryRepository;

    @Override
    public void run(String... args) throws Exception {

        List<TagCategory> tagCategories =  tagCategoryRepository.findAll();
        if (tagCategories.isEmpty()){
            List<TagCategory> list = Arrays.stream(TagCategoryEnum.values())
                    .map(tagCategoryEnum -> new TagCategory(tagCategoryEnum.getName(), tagCategoryEnum.getIsExpense()))
                    .toList();
            tagCategoryRepository.saveAll(list);
        }
    }
}
