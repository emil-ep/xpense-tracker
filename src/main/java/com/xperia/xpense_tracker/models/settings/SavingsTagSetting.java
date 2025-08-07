package com.xperia.xpense_tracker.models.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SavingsTagSetting extends AbstractUserSetting{

    private List<String> tags;
}
