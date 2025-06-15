package com.xperia.xpense_tracker.jobs;

import com.xperia.xpense_tracker.models.entities.JobStatus;
import com.xperia.xpense_tracker.models.entities.JobStatusEnum;
import com.xperia.xpense_tracker.services.JobStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("MutualFundTrackerJob")
public class MutualFundTrackerJob implements ScheduledJob{

    private static final Logger LOGGER = LoggerFactory.getLogger(MutualFundTrackerJob.class);

    private final RestTemplate restTemplate;

    private final JobStatusService jobStatusService;

//    @Value("${mutualFund.api.url}")
//    private String mutualFundUrl;
//
//    @Value("${mutualFund.job.enabled}")
//    private boolean jobEnabled;

    @Autowired
    public MutualFundTrackerJob(RestTemplate restTemplate, JobStatusService jobStatusService){
        this.restTemplate = restTemplate;
        this.jobStatusService = jobStatusService;
    }

    @Override
    public String getName() {
        return "MutualFundTrackerJob";
    }

    @Override
    public void execute() {
        LOGGER.info("Executing MutualFundTrackerJob");
        JobStatus jobStatus = new JobStatus("MutualFundTrackerJob", System.currentTimeMillis(), JobStatusEnum.STARTED);
        jobStatus = jobStatusService.saveStatus(jobStatus);
//        List<MutualFundScheme> response = restTemplate.getForObject(mutualFundUrl, List.class);
//        if (response != null){
//            LOGGER.info("got data : {}", response.size());
//        }
        jobStatus.setStatus(JobStatusEnum.COMPLETED);
        jobStatusService.saveStatus(jobStatus);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}
