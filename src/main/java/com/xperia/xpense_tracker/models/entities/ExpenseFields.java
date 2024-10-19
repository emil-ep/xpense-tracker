package com.xperia.xpense_tracker.models.entities;


public enum ExpenseFields {

    TRANSACTION_DATE("transactionDate"),
    DESCRIPTION("description"),
    BANK_REF_NO("bankReferenceNo"),
    DEBIT("debit"),
    CREDIT("credit"),
    CLOSING_BALANCE("closingBalance");


    private final String fieldName;

    ExpenseFields(String fieldName){
        this.fieldName = fieldName;
    }

    public String getFieldName(){
        return this.fieldName;
    }
}
