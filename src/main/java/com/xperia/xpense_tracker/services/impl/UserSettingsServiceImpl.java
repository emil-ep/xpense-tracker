package com.xperia.xpense_tracker.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerNotFoundException;
import com.xperia.xpense_tracker.exception.customexception.TrackerUnknownException;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.entities.UserSettings;
import com.xperia.xpense_tracker.models.request.UserSettingUpdateItem;
import com.xperia.xpense_tracker.models.settings.SettingsType;
import com.xperia.xpense_tracker.repository.UserSettingRepository;
import com.xperia.xpense_tracker.services.UserService;
import com.xperia.xpense_tracker.services.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserSettingsServiceImpl implements UserSettingsService {

    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<UserSettings> fetchUserSettings(String username) {
        Optional<TrackerUser> user = userService.findUserByEmail(username);
        if (user.isEmpty()){
            throw new TrackerNotFoundException("User not found");
        }
        return userSettingRepository.findAllByUserId(user.get().getId());
    }

    @Override
    public List<UserSettings> updateUserSettings(List<UserSettingUpdateItem> items, UserDetails userDetails) {
        List<UserSettings> updatedSettings = new ArrayList<>();
        items.forEach(item -> {
            UserSettings updatedSetting = updateUserSettings(SettingsType.findByType(item.getType()), item.getPayload(), userDetails);
            updatedSettings.add(updatedSetting);
        });
        return updatedSettings;
    }

    @Override
    public UserSettings updateUserSettings(SettingsType type, Object payload, UserDetails userDetails) {
        Optional<TrackerUser> user = userService.findUserByEmail(userDetails.getUsername());
        if (user.isEmpty()){
            throw new TrackerNotFoundException("User not found");
        }
        Optional<UserSettings> userSettings = userSettingRepository.findByUserAndType(user.get(), type);
        if (userSettings.isEmpty()){
            throw new TrackerNotFoundException("The provided settingsType is not available");
        }
        JsonNode node = objectMapper.valueToTree(payload);
        if (!validatePayload(node, type)){
            throw new TrackerBadRequestException("Validation failed");
        }
        userSettings.get().setPayload(node);
        return userSettingRepository.save(userSettings.get());
    }

    @Override
    public UserSettings findUserSettingsByType(SettingsType type, UserDetails userDetails) {
        Optional<TrackerUser> user = userService.findUserByEmail(userDetails.getUsername());
        if (user.isEmpty()){
            throw new TrackerNotFoundException("User not found");
        }
        Optional<UserSettings> userSettings = userSettingRepository.findByUserAndType(user.get(), type);
        if (userSettings.isEmpty()){
            throw new TrackerNotFoundException("The provided settingsType is not available");
        }
        return userSettings.get();
    }

    private boolean validatePayload(JsonNode payload, SettingsType type){
        try{
            objectMapper.treeToValue(payload, type.getPayloadClass());
            return true;
        }catch (MismatchedInputException ex){
            throw new TrackerBadRequestException("Payload structure is invalid for type: " + type);
        }catch (Exception ex){
            throw new TrackerUnknownException("Failed to parse payload");
        }
    }
}
