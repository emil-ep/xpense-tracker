package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.services.GoogleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xperia.client.GoogleClient;
import org.xperia.models.google.GoogleMailLabel;
import org.xperia.models.google.GoogleMailLabelResponse;

import java.util.List;

@Service
public class GoogleServiceImpl implements GoogleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleServiceImpl.class);

    private final GoogleClient googleClient;

    @Autowired
    public GoogleServiceImpl(GoogleClient googleClient){
        this.googleClient = googleClient;
    }

    @Override
    public List<GoogleMailLabel> fetchLabels(String oauth2Token, String email) {
        try{
            GoogleMailLabelResponse response =  this.googleClient.getLabelIds(oauth2Token);
            if (response != null){
                return response.labels();
            }
            LOGGER.debug("Received Google Mail Label response as null when parsed");
            return null;
        }catch (Exception ex){
            LOGGER.error("Error fetching labels for user : {}", email, ex);
            return null;
        }
    }
}
