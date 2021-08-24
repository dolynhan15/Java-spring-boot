package com.qooco.boost.business.impl;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/11/2018 - 5:57 PM
 */

import com.qooco.boost.business.BusinessJobService;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.entities.Job;
import com.qooco.boost.data.oracle.services.CompanyService;
import com.qooco.boost.data.oracle.services.JobService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.JobDTO;
import com.qooco.boost.models.request.JobReq;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BusinessJobServiceImpl implements BusinessJobService {
    @Autowired
    private JobService jobService;

    @Autowired
    private CompanyService companyService;

    @Override
    public BaseResp getGlobalJobs(Authentication authentication) {
        List<Job> jobs = jobService.getJobsByCompanyNull();
        List<JobDTO> jobsDTO = jobs.stream().map(it -> new JobDTO(it, getLocale(authentication))).collect(Collectors.toList());
        return new BaseResp<>(jobsDTO);
    }

    @Override
    public BaseResp getJobsByCompany(Long companyId, Authentication authentication) {
        if (Objects.isNull(companyId)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }

        List<Job> jobs = jobService.getJobsByCompany(companyId);
        List<JobDTO> jobsDTO = jobs.stream().map(it -> new JobDTO(it, getLocale(authentication))).collect(Collectors.toList());
        return new BaseResp<>(jobsDTO);
    }

    @Override
    public BaseResp createNewJob(JobReq job, Authentication authentication) {
        this.validateJob(job);
        job.setJobName(StringUtil.format(job.getJobName()));

        if (Objects.isNull(job.getCompanyId())) {
            throw new InvalidParamException(ResponseStatus.COMPANY_ID_IS_EMPTY);
        }
        if (StringUtils.isBlank(job.getJobDescription())) {
            job.setJobDescription(job.getJobName());
        }
        Company foundCompany = companyService.findById(job.getCompanyId());
        if (Objects.isNull(foundCompany)) {
            throw new InvalidParamException(ResponseStatus.NOT_FOUND_COMPANY);
        }

        List<Job> existedJobs = jobService.checkJobExistedInSystem(job.getCompanyId(),job.getJobName().toLowerCase());
        if (CollectionUtils.isNotEmpty(existedJobs)) {
            return new BaseResp<>(new JobDTO(existedJobs.get(0), getLocale(authentication)));
        }

        Job createdJob = jobService.save(job.convertToJob());
        if (Objects.isNull(createdJob)) {
            throw new InvalidParamException(ResponseStatus.SAVE_FAIL);
        }
        return new BaseResp<>(new JobDTO(createdJob, getLocale(authentication)));
    }

    private void validateJob(JobReq job) {
        if (Objects.isNull(job)) {
            throw new InvalidParamException(ResponseStatus.JOB_IS_EMPTY);
        }

        if (StringUtils.isBlank(job.getJobName())) {
            throw new InvalidParamException(ResponseStatus.JOB_NAME_IS_EMPTY);
        }

        if (job.getJobName().trim().length() < 2) {
            throw new InvalidParamException(ResponseStatus.JOB_NAME_IS_TOO_SHORT);
        }

        if (job.getJobName().trim().length() > 255) {
            throw new InvalidParamException(ResponseStatus.JOB_NAME_IS_TOO_LONG);
        }

        if (StringUtils.isNotBlank(job.getJobDescription()) && job.getJobDescription().trim().length() < 2) {
            throw new InvalidParamException(ResponseStatus.JOB_DESCRIPTION_IS_TOO_SHORT);
        }

        if (StringUtils.isNotBlank(job.getJobDescription()) && job.getJobDescription().trim().length() > 1000) {
            throw new InvalidParamException(ResponseStatus.JOB_DESCRIPTION_IS_TOO_LONG);
        }
    }
}
