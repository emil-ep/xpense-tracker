package com.xperia.xpense_tracker.models.entities;

public enum MetricTimeFrame {

    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly"),
    CUSTOM("custom");


    private final String timeframe;

    MetricTimeFrame(String timeframe){
        this.timeframe = timeframe;
    }


    public static MetricTimeFrame findByTimeframe(String timeframe){
        for (MetricTimeFrame metricTimeFrame : MetricTimeFrame.values()){
            if (metricTimeFrame.timeframe.equalsIgnoreCase(timeframe)){
                return metricTimeFrame;
            }
        }
        return null;
    }

    public String getTimeframe(){
        return this.timeframe;
    }

}
