package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.Job;
import lombok.*;

import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class JobEmbedded {
    private Long id;
    private String name;
    private String description;

    public JobEmbedded(JobEmbedded jobEmbedded) {
        if (Objects.nonNull(jobEmbedded)) {
            id = jobEmbedded.getId();
            name = jobEmbedded.getName();
            description = jobEmbedded.getDescription();
        }
    }

    public JobEmbedded(Job job) {
        if (Objects.nonNull(job)) {
            this.id = job.getJobId();
            this.name = job.getJobName();
            this.description = job.getJobDescription();
        }
    }
}
