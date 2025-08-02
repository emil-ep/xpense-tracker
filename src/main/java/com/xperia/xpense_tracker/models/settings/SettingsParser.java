package com.xperia.xpense_tracker.models.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xperia.xpense_tracker.models.entities.Settings;

public class SettingsParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T parsePayload(Settings settings, Class<T> targetClass){
        try{
            return objectMapper.treeToValue(settings.getPayload(), targetClass);
        }catch (Exception ex){
            throw new RuntimeException("Failed to parse payload", ex);
        }
    }
}
