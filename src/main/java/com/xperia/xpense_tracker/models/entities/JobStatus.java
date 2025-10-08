package com.xperia.xpense_tracker.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.xperia.models.JobStatusEnum;

@Entity(name = "job_status")
@NoArgsConstructor
@Getter
@Setter
public class JobStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String jobName;

    private long lastRun;

    @Enumerated(EnumType.STRING)
    private JobStatusEnum status;

    public JobStatus(String jobName, long lastRun, JobStatusEnum status){
        this.jobName = jobName;
        this.lastRun = lastRun;
        this.status = status;
    }

}
