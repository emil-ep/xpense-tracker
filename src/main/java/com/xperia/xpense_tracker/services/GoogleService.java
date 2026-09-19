package com.xperia.xpense_tracker.services;

import org.xperia.models.google.GoogleMailLabel;
import org.xperia.models.google.GoogleProfileResponse;

import java.util.List;

public interface GoogleService {

    List<GoogleMailLabel> fetchLabels(String oauth2Token, String email);

    GoogleProfileResponse fetchUserProfile(String oauth2Token, String email);
}
