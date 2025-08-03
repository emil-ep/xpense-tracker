package com.xperia.xpense_tracker.services;


import com.xperia.xpense_tracker.models.entities.UserSettings;

import java.util.List;

public interface UserSettingsService {

    List<UserSettings> fetchUserSettings(String username);
}
