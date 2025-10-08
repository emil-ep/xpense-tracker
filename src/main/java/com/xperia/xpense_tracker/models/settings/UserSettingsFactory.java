package com.xperia.xpense_tracker.models.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xperia.xpense_tracker.models.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.xperia.exception.TrackerException;

import java.util.ArrayList;

public class UserSettingsFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSettingsFactory.class);

    public static JsonNode createUserSettings(SettingsType type){
        switch (type) {
            case CURRENCY -> {
                CurrencySetting setting = new CurrencySetting(Currency.INR.getName());
                try{
                    String settingAsString = objectMapper.writeValueAsString(setting);
                    return objectMapper.readTree(settingAsString);
                }catch (JsonProcessingException ex){
                    LOGGER.error("Error while parsing CURRENCY settings : {}", ex.getMessage(), ex);
                    throw new TrackerException("unable to parse setting", HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            }
            case SAVINGS_TAGS -> {
                SavingsTagSetting setting = new SavingsTagSetting(new ArrayList<>());
                try{
                    String settingAsString = objectMapper.writeValueAsString(setting);
                    return objectMapper.readTree(settingAsString);
                }catch (JsonProcessingException ex){
                    LOGGER.error("Error while parsing SAVINGS_TAG settings : {}", ex.getMessage(), ex);
                    throw new TrackerException("unable to parse setting", HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            }
            case USERNAME -> {
                UsernameSetting setting = new UsernameSetting("");
                try{
                    String settingAsString = objectMapper.writeValueAsString(setting);
                    return objectMapper.readTree(settingAsString);
                }catch (JsonProcessingException ex){
                    LOGGER.error("Error while parsing USERNAME settings : {}", ex.getMessage(), ex);
                    throw new TrackerException("unable to parse setting", HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            }
            case null, default -> {
                return null;
            }
        }
    }
}
