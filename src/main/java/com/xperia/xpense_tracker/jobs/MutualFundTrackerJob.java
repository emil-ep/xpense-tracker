package com.xperia.xpense_tracker.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutualFundTrackerJob implements ScheduledJob{

    private static final Logger LOGGER = LoggerFactory.getLogger(MutualFundTrackerJob.class);

    @Override
    public String getName() {
        return "MutualFundTrackerJob";
    }

    @Override
    public void execute() {
        LOGGER.info("Executing MutualFundTrackerJob");
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getCronExpression() {
        return "";
    }
}
