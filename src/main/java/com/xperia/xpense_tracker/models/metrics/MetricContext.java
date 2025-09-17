package com.xperia.xpense_tracker.models.metrics;


import com.xperia.xpense_tracker.models.entities.TagCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetricContext {

    private List<TagCategoryEnum> userSavingsCategories;

}
