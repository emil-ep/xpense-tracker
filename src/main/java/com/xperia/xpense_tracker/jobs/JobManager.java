package com.xperia.xpense_tracker.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JobManager implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobManager.class);

    private final ThreadPoolTaskScheduler scheduler;
    private final Map<String, ScheduledJob> jobs;

    @Autowired
    public JobManager(ThreadPoolTaskScheduler scheduler, Map<String, ScheduledJob> jobs){
        this.scheduler = scheduler;
        this.jobs = jobs;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    private void scheduleJob(String name, String cronExpression){
        ScheduledJob job = jobs.get(name);

    }
}
