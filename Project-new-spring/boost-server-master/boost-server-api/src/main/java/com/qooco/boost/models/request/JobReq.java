package com.qooco.boost.models.request;

import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.entities.Job;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class JobReq {
    private String jobName;
    private String jobDescription;
    private Long companyId;

    public JobReq(String jobName, Long companyId) {
        this.jobName = jobName;
        this.companyId = companyId;
    }

    public Job convertToJob() {
        Job job = new Job();
        job.setJobName(jobName);
        job.setJobDescription(jobDescription);
        job.setCompany(new Company(companyId));
        return job;
    }
}