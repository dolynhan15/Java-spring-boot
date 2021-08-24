package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.Job;
import com.qooco.boost.data.oracle.reposistories.JobRepository;
import com.qooco.boost.data.oracle.services.JobService;
import com.qooco.boost.data.oracle.services.impl.JobServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/11/2018 - 6:12 PM
 */
@RunWith(PowerMockRunner.class)
public class JobServiceImplTest {

    @InjectMocks
    private JobService jobService = new JobServiceImpl();

    @Mock
    private JobRepository jobRepository = Mockito.mock(JobRepository.class);

    @Test
    public void getJobsByCompany_whenDoNotHaveJobCompany_thenReturnJobsNotBelongToAnyCompany() {
        List<Job> responseJobs = new ArrayList<>();
        responseJobs.add(new Job(1L));
        responseJobs.add(new Job(2L));

        Mockito.when(jobRepository.findByCompanyCompanyId(10L))
                .thenReturn(responseJobs);

        List<Job> actualResp = jobService.getJobsByCompany(10L);
        Assert.assertEquals(responseJobs.size(), actualResp.size());
    }

    @Test
    public void getJobsByCompany_whenHaveJobCompany_thenReturnJobsCompanyAndJobsNotBelongToAnyCompany() {
        List<Job> responseJobs = new ArrayList<>();
        responseJobs.add(new Job(10L));
        responseJobs.add(new Job(1L));
        responseJobs.add(new Job(2L));

        Mockito.when(jobRepository.findByCompanyCompanyId(10L))
                .thenReturn(responseJobs);

        List<Job> actualResp = jobService.getJobsByCompany(10L);
        Assert.assertEquals(responseJobs.size(), actualResp.size());
    }

    @Test
    public void findByCompanyIsNull_whenCompanyNull_thenReturnJobsHaveCompanyNull() {
        List<Job> responseJobs = new ArrayList<>();
        responseJobs.add(new Job(1L));
        responseJobs.add(new Job(2L));

        Mockito.when(jobRepository.findByCompanyIsNull())
                .thenReturn(responseJobs);

        List<Job> actualResp = jobService.getJobsByCompanyNull();
        Assert.assertEquals(responseJobs.size(), actualResp.size());
    }

    @Test
    public void save_whenJobIsEmpty_thenReturnNull() {
        Mockito.when(jobRepository.save(new Job())).thenReturn(null);
        Assert.assertNull(jobService.save(new Job()));
    }

    @Test
    public void save_whenJobIsValid_thenReturnSuccess() {
        Job job = new Job("Cruise Ship Attendant");
        Mockito.when(jobRepository.save(job)).thenReturn(job);
        Assert.assertEquals(job.getJobName(), jobService.save(job).getJobName());
    }
}