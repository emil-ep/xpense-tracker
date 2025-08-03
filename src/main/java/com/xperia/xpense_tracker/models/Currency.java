package com.xperia.xpense_tracker.models;

import lombok.Getter;

@Getter
public enum Currency {

    INR("INR", "India"),
    USD("USD", "United Stated of America"),
    EUR("EUR", "20 countries of the European union"),
    JPY("JPY", "Japan"),
    GBP("GBP", "Britain Pound Sterling");


    private String country;

    private String name;


    Currency(String name, String country){
        this.country = country;
        this.name = name;
    }
}
