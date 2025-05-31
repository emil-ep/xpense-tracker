package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.JobStatus;
import com.xperia.xpense_tracker.repository.JobStatusRepository;
import com.xperia.xpense_tracker.services.JobStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobStatusServiceImpl implements JobStatusService {

    @Autowired
    private JobStatusRepository jobStatusRepository;

    @Override
    public JobStatus saveStatus(JobStatus jobStatus) {

        return jobStatusRepository.save(jobStatus);
    }
}
