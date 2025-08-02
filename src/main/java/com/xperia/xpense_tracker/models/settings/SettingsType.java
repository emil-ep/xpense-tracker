package com.xperia.xpense_tracker.models.settings;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum SettingsType{

    CURRENCY("currency", "The currency preferred by the user"),
    SAVINGS_TAGS("savingsTags", "The tags preferred by the user to be computed as savings. The debit incurred by these tags are not considered in computing expense");

    private final String type;
    private final String description;

    SettingsType(String type, String description){
        this.type = type;
        this.description = description;
    }

    public SettingsType findByType(String type){
        Optional<SettingsType> setting = Arrays.stream(SettingsType.values())
                .filter(settingsType -> settingsType.getType().equalsIgnoreCase(type))
                .findAny();
        return setting.orElse(null);
    }

}
