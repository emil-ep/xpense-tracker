package com.xperia.xpense_tracker.services;


import com.xperia.xpense_tracker.models.entities.UserSettings;
import com.xperia.xpense_tracker.models.settings.SettingsType;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserSettingsService {

    List<UserSettings> fetchUserSettings(String username);

    UserSettings updateUserSettings(SettingsType type, Object payload, UserDetails userDetails);
}
