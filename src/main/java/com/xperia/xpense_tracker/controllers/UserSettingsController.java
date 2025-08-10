package com.xperia.xpense_tracker.controllers;
import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerException;
import com.xperia.xpense_tracker.models.entities.UserSettings;
import com.xperia.xpense_tracker.models.request.UserSettingUpdateItem;
import com.xperia.xpense_tracker.models.request.UserSettingUpdateRequest;
import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.models.settings.SettingsType;
import com.xperia.xpense_tracker.models.settings.UserSettingsFactory;
import com.xperia.xpense_tracker.services.UserSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user/settings")
public class UserSettingsController {

    @Autowired
    private UserSettingsService userSettingsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSettingsFactory.class);

    @GetMapping
    public ResponseEntity<AbstractResponse> fetchUserSettings(@AuthenticationPrincipal UserDetails userDetails){
        try{
            List<UserSettings> userSettingsList = userSettingsService.fetchUserSettings(userDetails.getUsername());
            return ResponseEntity.ok(new SuccessResponse(userSettingsList));
        }catch (Exception ex){
            LOGGER.error("Error fetching user settings : {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error fetching user settings"));
        }
    }

    @PutMapping
    public ResponseEntity<AbstractResponse> updateUserSettings(@RequestBody UserSettingUpdateRequest request,
                                                               @AuthenticationPrincipal UserDetails userDetails){
        try{
            List<UserSettingUpdateItem> itemsToUpdate = request.getItems();
            if (itemsToUpdate == null || itemsToUpdate.isEmpty()){
                throw new TrackerBadRequestException("No items to update");
            }
            itemsToUpdate.forEach(item -> {
                SettingsType type = SettingsType.findByType(item.getType());
                if (type == null) {
                    throw new TrackerBadRequestException("Provided type value is not available");
                }
            });

            List<UserSettings> settings = userSettingsService.updateUserSettings(itemsToUpdate, userDetails);
            return ResponseEntity.ok(new SuccessResponse(settings));
        }catch (TrackerException ex){
            LOGGER.error("Faced error in updating user settings : {}", ex.getMessage());
            throw ex;
        } catch (Exception ex){
            LOGGER.error("Error while updating user settings : {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error updating user settings"));
        }
    }
}
