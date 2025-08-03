package com.xperia.xpense_tracker.services.impl;


import com.xperia.xpense_tracker.exception.customexception.TrackerNotFoundException;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.entities.UserSettings;
import com.xperia.xpense_tracker.repository.UserSettingRepository;
import com.xperia.xpense_tracker.services.UserService;
import com.xperia.xpense_tracker.services.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserSettingsServiceImpl implements UserSettingsService {

    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private UserService userService;

    @Override
    public List<UserSettings> fetchUserSettings(String username) {
        Optional<TrackerUser> user = userService.findUserByEmail(username);
        if (user.isEmpty()){
            throw new TrackerNotFoundException("User not found");
        }
        return userSettingRepository.findAllByUserId(user.get().getId());
    }
}
