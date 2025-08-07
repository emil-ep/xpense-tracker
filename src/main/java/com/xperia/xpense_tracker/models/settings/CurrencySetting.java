package com.xperia.xpense_tracker.models.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CurrencySetting extends AbstractUserSetting{

    private String userCurrency;


}
