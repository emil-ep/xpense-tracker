package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.cache.CacheNames;
import com.xperia.xpense_tracker.cache.CacheService;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.entities.tracker.UserRole;
import com.xperia.xpense_tracker.models.entities.tracker.UserSettings;
import com.xperia.xpense_tracker.models.request.SignUpRequest;
import com.xperia.xpense_tracker.models.settings.SettingsType;
import com.xperia.xpense_tracker.models.settings.UserSettingsFactory;
import com.xperia.xpense_tracker.repository.tracker.UserRepository;
import com.xperia.xpense_tracker.repository.tracker.UserSettingRepository;
import com.xperia.xpense_tracker.services.AuthService;
import com.xperia.xpense_tracker.services.JwtService;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private CacheService cacheService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private void validateSignIn(String email) throws BadRequestException {
        if(userRepository.findByEmail(email).isEmpty()){
            throw new BadRequestException("User with the provided email not present");
        }
    }

    @Override
    public String signInUser(String userName, String password) throws BadRequestException{
        validateSignIn(userName);
        var user = userRepository.findByEmail(userName).get();
        if(encoder.matches(password, user.getPassword())){
            return jwtService.generateToken(user);
        }else{
            throw new BadRequestException("Entered username or password doesn't match");
        }
    }

    private void validateSignUp(String email) throws BadRequestException{

        if(userRepository.findByEmail(email).isPresent()){
            throw new BadRequestException("User with the same email already present");
        }

    }

    public TrackerUser signUpUser(SignUpRequest signUpRequest) throws BadRequestException{
        validateSignUp(signUpRequest.getEmail());
        TrackerUser user = new TrackerUser(signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), signUpRequest.getName(), UserRole.USER);
        userRepository.save(user);
        Arrays.stream(SettingsType.values()).forEach(type -> {
            Optional<UserSettings> userSetting = userSettingRepository.findByUserAndType(user, type);
            if (userSetting.isEmpty()){
                UserSettings newUserSetting = new UserSettings(type, user, UserSettingsFactory.createUserSettings(type));
                userSettingRepository.save(newUserSetting);
            }
        });
        return user;
    }

    @Override
    public void logoutUser(TrackerUser user) {
        cacheService.clearCache(CacheNames.METRICS_CACHE_NAME, user.getId());
        LOGGER.info("User {} logged out", user.getEmail());
    }
}
