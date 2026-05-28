package com.xperia.xpense_tracker.jobs;

import org.springframework.stereotype.Component;

@Component("DataBackupJob")
public class DataBackupJob implements ScheduledJob{



    @Override
    public String getName() {
        return "DataBackupJob";
    }

    @Override
    public void execute() {
        //TODO - backup to Google drive ?
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
