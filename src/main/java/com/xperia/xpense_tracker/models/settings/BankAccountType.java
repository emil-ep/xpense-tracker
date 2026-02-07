package com.xperia.xpense_tracker.models.settings;

import lombok.Getter;

@Getter
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

    public static BankAccountType findByShortName(String name){
        for(BankAccountType accountType: BankAccountType.values()){
            if (accountType.getShortName().equalsIgnoreCase(name)){
                return accountType;
            }
        }
        return null;
    }
}
