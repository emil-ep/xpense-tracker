package com.xperia.xpense_tracker.models;

public enum ExpenseAggregateType {

    DAY("daily"),
    MONTH("monthly"),
    YEAR("yearly");


    private final String type;

    ExpenseAggregateType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public static ExpenseAggregateType findByType(String type){
        for(ExpenseAggregateType aggregateType: ExpenseAggregateType.values()){
            if (aggregateType.getType().equals(type)){
                return aggregateType;
            }
        }
        return null;
    }
}
