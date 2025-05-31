package com.xperia.xpense_tracker.jobs;

import com.xperia.xpense_tracker.jobs.models.MutualFundScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component("MutualFundTrackerJob")
public class MutualFundTrackerJob implements ScheduledJob{

    private static final Logger LOGGER = LoggerFactory.getLogger(MutualFundTrackerJob.class);

    private RestTemplate restTemplate;

    @Value("${mutualFund.api.url}")
    private String mutualFundUrl;

    @Value("${mutualFund.job.enabled}")
    private boolean jobEnabled;

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
        List<MutualFundScheme> response = restTemplate.getForObject(mutualFundUrl, List.class);
        if (response != null){
            LOGGER.info("got data : {}", response.size());
        }
    }

    @Override
    public boolean isEnabled() {
        return jobEnabled;
    }

}
