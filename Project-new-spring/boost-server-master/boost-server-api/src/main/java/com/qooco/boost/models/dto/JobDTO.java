package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.JobEmbedded;
import com.qooco.boost.data.oracle.entities.Job;
import com.qooco.boost.enumeration.ApplicationContextHolder;
import com.qooco.boost.localization.JobDatabaseMessageSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobDTO {
    private Long jobId;
    private String jobName;
    private String jobDescription;

    public JobDTO(Job job, String locale) {
        if (Objects.nonNull(job)) {
            this.jobId = job.getJobId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.jobName = ctx.getBean(JobDatabaseMessageSource.class).getMessage(job.getJobId().toString(), locale);
            }
            if (StringUtils.isBlank(jobName)) {
                this.jobName = job.getJobName();
            }
            this.jobDescription = jobName;
        }
    }

    public JobDTO(JobEmbedded job, String locale) {
        if (Objects.nonNull(job)) {
            this.jobId = job.getId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.jobName = ctx.getBean(JobDatabaseMessageSource.class).getMessage(job.getId().toString(), locale);
            }
            if (StringUtils.isBlank(jobName)) {
                this.jobName = job.getName();
            }
            this.jobDescription = jobName;
        }
    }
}
