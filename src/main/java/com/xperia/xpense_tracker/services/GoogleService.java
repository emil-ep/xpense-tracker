package com.xperia.xpense_tracker.services;

public interface GoogleService {

    String fetchLabels(String oauth2Token, String email);
}
