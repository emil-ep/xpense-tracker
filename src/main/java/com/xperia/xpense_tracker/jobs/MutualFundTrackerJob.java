package com.xperia.xpense_tracker.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("MutualFundTrackerJob")
public class MutualFundTrackerJob implements ScheduledJob{

    private static final Logger LOGGER = LoggerFactory.getLogger(MutualFundTrackerJob.class);

    private RestTemplate restTemplate;

    @Autowired
    public MutualFundTrackerJob(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

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

}
