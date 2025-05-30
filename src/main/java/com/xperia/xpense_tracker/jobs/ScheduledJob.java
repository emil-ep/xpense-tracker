package com.xperia.xpense_tracker.jobs;

public interface ScheduledJob {

    String getName();

    void execute();

    boolean isEnabled();

}
