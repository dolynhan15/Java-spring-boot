package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qooco.boost.data.mongo.embedded.JobEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.oracle.entities.CurriculumVitaeJob;
import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.models.dto.JobDTO;
import com.qooco.boost.models.dto.currency.CurrencyDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortUserCVDTO extends BaseUserCVDTO {
    @Setter
    @Getter
    private double minSalary;
    @Setter
    @Getter
    private double maxSalary;
    @Setter
    @Getter
    private List<JobDTO> jobs;
    @Setter
    @Getter
    private CurrencyDTO currency;

    private boolean isFullTime;
    private boolean isHourSalary;

    public boolean isHourSalary() {
        return isHourSalary;
    }

    @JsonProperty("isHourSalary")
    public void setHourSalary(boolean hourSalary) {
        isHourSalary = hourSalary;
    }

    public boolean isFullTime() {
        return isFullTime;
    }

    @JsonProperty("isFullTime")
    public void setFullTime(boolean fullTime) {
        isFullTime = fullTime;
    }

    public ShortUserCVDTO(UserProfileCvEmbedded userProfile, String locale) {
        super(userProfile, locale);
        if (Objects.nonNull(userProfile)) {
            this.minSalary = userProfile.getMinSalary();
            this.maxSalary = userProfile.getMaxSalary();
            this.jobs = getJobDOTSFromEmbedded(userProfile.getJobs(), locale);
            this.isFullTime = userProfile.isFullTime();
            this.isHourSalary = userProfile.isHourSalary();
            ofNullable(userProfile.getCurrency()).ifPresent(it -> this.currency = new CurrencyDTO(it, locale));
        }
    }

    public ShortUserCVDTO(UserCurriculumVitae curriculumVitae, String locale) {
        super(curriculumVitae, locale);
        if (Objects.nonNull(curriculumVitae)) {
            this.minSalary = curriculumVitae.getMinSalary();
            this.maxSalary = curriculumVitae.getMaxSalary();
            this.jobs = getJobDOTSFromCVJob(curriculumVitae.getCurriculumVitaeJobs(), locale);
            this.isFullTime = curriculumVitae.isFullTime();
            this.isHourSalary = curriculumVitae.isHourSalary();
            ofNullable(curriculumVitae.getCurrency()).ifPresent(it -> this.currency = new CurrencyDTO(it, locale));
        }
    }

    public ShortUserCVDTO(UserCvDoc userCvDoc, String locale) {
        super(userCvDoc, locale);
        if (Objects.nonNull(userCvDoc)) {
            this.minSalary = userCvDoc.getMinSalary();
            this.maxSalary = userCvDoc.getMaxSalary();
            this.jobs = getJobDOTSFromEmbedded(userCvDoc.getJobs(), locale);
            this.isFullTime = userCvDoc.isFullTime();
            this.isHourSalary = userCvDoc.isHourSalary();
            ofNullable(userCvDoc.getCurrency()).ifPresent(it -> this.currency = new CurrencyDTO(it, locale));
        }
    }

    private List<JobDTO> getJobDOTSFromCVJob(List<CurriculumVitaeJob> jobs, String locale) {
        if (CollectionUtils.isNotEmpty(jobs)) {
            return jobs.stream().map(cj -> new JobDTO(cj.getJob(), locale)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    protected List<JobDTO> getJobDOTSFromEmbedded(List<JobEmbedded> jobs, String locale) {
        return ofNullable(jobs).map(it -> it.stream().map(item -> new JobDTO(item, locale)).collect(Collectors.toList())).orElseGet(ArrayList::new);
    }


}
