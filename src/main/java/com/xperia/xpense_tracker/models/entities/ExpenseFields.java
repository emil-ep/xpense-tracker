package com.xperia.xpense_tracker.models.entities;


import java.util.Arrays;
import java.util.List;

public enum ExpenseFields {

    TRANSACTION_DATE("transactionDate", Arrays.asList("txnDate", "date", "txn date")),
    DESCRIPTION("description", Arrays.asList("narration", "description")),
    BANK_REF_NO("bankReferenceNo", Arrays.asList("Chq/Ref Number", "Ref Number")),
    DEBIT("debit", Arrays.asList("debit", "debit amount", "debit amnt")),
    CREDIT("credit", Arrays.asList("credit", "credit amnt", "credit amount")),
    CLOSING_BALANCE("closingBalance", Arrays.asList("closing balance", "closing bal"));


    private final String fieldName;

    private final List<String> nameDictionary;

    ExpenseFields(String fieldName, List<String> nameDictionary){
        this.fieldName = fieldName;
        this.nameDictionary = nameDictionary;
    }

    public String getFieldName(){
        return this.fieldName;
    }

    public List<String> getNameDictionary() {
        return nameDictionary;
    }

    public static ExpenseFields findMatchingField(String name){
        for (ExpenseFields field: ExpenseFields.values()){
            if (field.nameDictionary.stream().anyMatch(nameDict -> nameDict.equalsIgnoreCase(name))) {
                return field;
            }
        }
        return null;
    }
}
