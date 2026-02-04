package com.xperia.xpense_tracker.models.settings;

public enum BankAccountType {

    SBI("SBI", "State Bank of India"),
    HDFC("HDFC", "Housing development Finance corporation Limited"),
    ICICI("ICICI", "Industrial Credit and Investment Corporation of India"),
    CANARA("CANARA", "Canara Bank"),
    FEDERAL("FEDERAL", "The Federal Bank Limited"),
    UCO("UCO", "United Commercial Bank"),
    NONE("None", "");


    private String shortName;

    private String name;

    BankAccountType(String shortName, String name){
        this.shortName = shortName;
        this.name = name;
    }

}
