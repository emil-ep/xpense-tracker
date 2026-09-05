package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.services.GoogleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xperia.client.GoogleClient;

@Service
public class GoogleServiceImpl implements GoogleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleServiceImpl.class);

    private final GoogleClient googleClient;

    @Autowired
    public GoogleServiceImpl(GoogleClient googleClient){
        this.googleClient = googleClient;
    }

    @Override
    public String fetchLabels(String oauth2Token, String email) {
        try{
            return this.googleClient.getLabelIds(oauth2Token);
        }catch (Exception ex){
            LOGGER.error("Error fetching labels for user : {}", email, ex);
            return null;
        }
    }
}
