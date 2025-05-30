package com.xperia.xpense_tracker.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class JobManager implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobManager.class);

    private final ThreadPoolTaskScheduler scheduler;
    private final Map<String, ScheduledJob> jobs;
    private final Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    @Autowired
    public JobManager(ThreadPoolTaskScheduler scheduler, Map<String, ScheduledJob> jobs){
        this.scheduler = scheduler;
        this.jobs = jobs;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduleJob("MutualFundTrackerJob", "*/5 * * * * *");
    }

    private void scheduleJob(String name, String cronExpression){
        ScheduledJob job = jobs.get(name);
        if (job == null){
            LOGGER.error("No job found with name : {}", name);
            return;
        }
        if (job.isEnabled()) {
            ScheduledFuture<?> future = scheduler.schedule(job::execute, new CronTrigger(cronExpression));
            tasks.put(name, future);
        }
    }
}
