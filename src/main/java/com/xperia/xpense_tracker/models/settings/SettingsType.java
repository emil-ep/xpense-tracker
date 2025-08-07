package com.xperia.xpense_tracker.models.settings;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum SettingsType{

    CURRENCY("currency", "The currency preferred by the user", CurrencySetting.class),
    SAVINGS_TAGS("savingsTags", "The tags preferred by the user to be computed as savings. " +
            "The debit incurred by these tags are not considered in computing expense", SavingsTagSetting.class);

    private final String type;
    private final String description;
    private final Class<?> payloadClass;

    SettingsType(String type, String description, Class<?> payloadClass){
        this.type = type;
        this.description = description;
        this.payloadClass = payloadClass;
    }

    public static SettingsType findByType(String type){
        Optional<SettingsType> setting = Arrays.stream(SettingsType.values())
                .filter(settingsType -> settingsType.getType().equalsIgnoreCase(type))
                .findAny();
        return setting.orElse(null);
    }

}
