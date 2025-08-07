package com.xperia.xpense_tracker.repository;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.entities.UserSettings;
import com.xperia.xpense_tracker.models.settings.SettingsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSettingRepository extends JpaRepository<UserSettings, String> {

    Optional<UserSettings> findByUserAndType(TrackerUser user, SettingsType type);

    List<UserSettings> findAllByUserId(String userId);

}
