package com.xperia.xpense_tracker.models.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CurrencySetting {

    private String[] availableCurrencies;

    private String userCurrency;


}
