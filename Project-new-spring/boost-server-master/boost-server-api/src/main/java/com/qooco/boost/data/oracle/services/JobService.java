package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.Job;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 10:06 AM
*/
public interface JobService {

    Job findById(long jobId);

    Job findValidById(long jobId);

    List<Job> findByIds(long[] lstJobId);

    List<Job> getJobsByCompany(Long companyId);

    List<Job> checkJobExistedInSystem(Long companyId, String jobNam);

    List<Job> getJobsByCompanyNull();

    Job save(Job job);

    Boolean exists(Long id);
}
