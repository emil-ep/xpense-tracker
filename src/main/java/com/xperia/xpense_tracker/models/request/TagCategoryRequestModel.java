package com.xperia.xpense_tracker.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TagCategoryRequestModel {

    private String id;

    private String name;

    private boolean expense;
}
