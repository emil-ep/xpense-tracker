package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.tracker.JobStatus;

public interface JobStatusService {

    JobStatus saveStatus(JobStatus jobStatus);
}
