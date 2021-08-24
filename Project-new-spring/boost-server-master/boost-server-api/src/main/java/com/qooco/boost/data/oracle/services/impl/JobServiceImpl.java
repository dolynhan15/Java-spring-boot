package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.Job;
import com.qooco.boost.data.oracle.reposistories.JobRepository;
import com.qooco.boost.data.oracle.services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 10:11 AM
*/
@Service
@Transactional
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;
    @Override
    public Job findById(long jobId) {
        return jobRepository.findById(jobId).orElse(null);
    }

    @Override
    public Job findValidById(long jobId) {
        return jobRepository.findValidById(jobId);
    }

    @Override
    public List<Job> findByIds(long[] lstJobId)
    {
        if (lstJobId == null || lstJobId.length == 0) {
            return new ArrayList<>();
        }
        return jobRepository.findByIds(lstJobId);
    }

    @Override
    public List<Job> getJobsByCompany(Long companyId) {
        if (Objects.isNull(companyId)) {
            return getJobsByCompanyNull();
        }
        return jobRepository.findByCompanyCompanyId(companyId);
    }

    @Override
    public List<Job> checkJobExistedInSystem(Long companyId, String jobName) {
        return jobRepository.findByCompanyCompanyIdAndJobName(companyId, jobName);
    }

    @Override
    public List<Job> getJobsByCompanyNull() {
        return jobRepository.findByCompanyIsNull();
    }

    @Override
    public Job save(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public Boolean exists(Long id) {
        return jobRepository.existsById(id);
    }
}
