package com.xperia.xpense_tracker.repository;

import com.xperia.xpense_tracker.models.entities.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobStatusRepository extends JpaRepository<JobStatus, String> {

}
