package com.xperia.xpense_tracker.services;


import com.xperia.xpense_tracker.models.entities.tracker.UserSettings;
import com.xperia.xpense_tracker.models.request.UserSettingUpdateItem;
import com.xperia.xpense_tracker.models.settings.SettingsType;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserSettingsService {

    List<UserSettings> fetchUserSettings(String username);

    List<UserSettings> updateUserSettings(List<UserSettingUpdateItem> items, UserDetails userDetails);

    UserSettings updateUserSettings(SettingsType type, Object payload, UserDetails userDetails);

    UserSettings findUserSettingsByType(SettingsType type, UserDetails userDetails);
}
