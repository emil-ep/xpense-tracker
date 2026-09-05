package com.xperia.xpense_tracker.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper;

    private final GoogleClient googleClient;

    @Autowired
    public GoogleServiceImpl(GoogleClient googleClient){
        this.googleClient = googleClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<GoogleMailLabel> fetchLabels(String oauth2Token, String email) {
        try{
            String response =  this.googleClient.getLabelIds(oauth2Token);
            GoogleMailLabelResponse parsedResp = this.objectMapper.readValue(response, GoogleMailLabelResponse.class);
            if (parsedResp != null){
                return parsedResp.labels();
            }
            LOGGER.debug("Received Google Mail Label response as null when parsed");
            return null;
        }catch (Exception ex){
            LOGGER.error("Error fetching labels for user : {}", email, ex);
            return null;
        }
    }
}
