package com.xperia.xpense_tracker.models.settings;


import com.xperia.xpense_tracker.models.entities.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SavingsTagSetting {

    private List<Tag> tags;
}
