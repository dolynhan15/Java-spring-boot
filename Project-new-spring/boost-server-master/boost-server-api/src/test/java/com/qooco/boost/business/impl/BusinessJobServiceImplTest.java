package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessJobService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.entities.Job;
import com.qooco.boost.data.oracle.services.CompanyService;
import com.qooco.boost.data.oracle.services.JobService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.JobDTO;
import com.qooco.boost.models.request.JobReq;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.qooco.boost.business.impl.BaseUserService.initAuthenticatedUser;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/11/2018 - 6:12 PM
 */
@RunWith(PowerMockRunner.class)
public class BusinessJobServiceImplTest {
    @InjectMocks
    private BusinessJobService businessJobService = new BusinessJobServiceImpl();

    @Mock
    private JobService jobService = Mockito.mock(JobService.class);

    @Mock
    private CompanyService companyService = Mockito.mock(CompanyService.class);

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    private static AuthenticatedUser authenticatedUser = initAuthenticatedUser();

    @Test
    public void getGlobalJobs_whenHaveJobCompanyNull_thenReturnJobsResp() {
        List<Job> jobList = new ArrayList<>();
        jobList.add(new Job(1L));
        jobList.add(new Job(2L));

        Mockito.when(jobService.getJobsByCompanyNull()).thenReturn(jobList);
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        List<JobDTO> jobDTOList = jobList.stream().map(it -> new JobDTO(it, "")).collect(Collectors.toList());

        List<Job> actualResponse = (List<Job>)businessJobService.getGlobalJobs(authentication).getData();
        Assert.assertEquals(jobDTOList.size(), actualResponse.size());
    }

    @Test
    public void getJobsByCompany_whenCompanyIdNull_thenReturnInvalidParamException() {
        try {
            businessJobService.getJobsByCompany(null, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.ID_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void getJobsByCompany_whenHaveCompany_thenReturnJobListResponse() {
        List<Job> jobs = new ArrayList<>();
        jobs.add(new Job(1L));
        jobs.add(new Job(2L));

        Mockito.when(jobService.getJobsByCompany(10L)).thenReturn(jobs);
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        List<Job> actualResp = (List<Job>)businessJobService.getJobsByCompany(10L, authentication).getData();

        Assert.assertEquals(jobs.size(), actualResp.size());
    }

    @Test
    public void createNewJob_whenJobIsNull_thenReturnJobIsEmptyError() {
        try {
            businessJobService.createNewJob(null, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.JOB_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void createNewJob_whenJobNameIsEmpty_thenReturnJobNameIsEmptyError() {
        JobReq jobReq = new JobReq();
        try {
            businessJobService.createNewJob(jobReq, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.JOB_NAME_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void createNewJob_whenJobNameIsTooShort_thenReturnJobNameTooShortError() {
        JobReq jobReq = new JobReq();
        jobReq.setJobName(StringUtils.repeat("a", 1));
        try {
            businessJobService.createNewJob(jobReq, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.JOB_NAME_IS_TOO_SHORT.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void createNewJob_whenJobNameIsTooLong_thenReturnJobNameTooLongError() {
        JobReq jobReq = new JobReq();
        jobReq.setJobName(StringUtils.repeat("a", 256));
        try {
            businessJobService.createNewJob(jobReq, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.JOB_NAME_IS_TOO_LONG.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void createNewJob_whenJobDescriptionIsTooShort_thenReturnJobDescriptionTooShortError() {
        JobReq jobReq = new JobReq();
        jobReq.setJobName("Dev");
        jobReq.setJobDescription(" d ");
        try {
            businessJobService.createNewJob(jobReq, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.JOB_DESCRIPTION_IS_TOO_SHORT.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void createNewJob_whenJobDescriptionIsTooLong_thenReturnJobDescriptionTooLongError() {
        JobReq jobReq = new JobReq();
        jobReq.setJobName("Dev");
        jobReq.setJobDescription(StringUtils.repeat("a", 1001));
        try {
            businessJobService.createNewJob(jobReq, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.JOB_DESCRIPTION_IS_TOO_LONG.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void createNewJob_whenCompanyIdIsEmpty_thenReturnCompanyIdIsEmptyError() {
        JobReq jobReq = new JobReq("Cruise Ship Attendant", null);
        try {
            businessJobService.createNewJob(jobReq, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.COMPANY_ID_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void createNewJob_whenCompanyIdIsNotExisted_thenReturnNotFoundCompanyError() {
        Mockito.when(companyService.findById(1L)).thenReturn(null);
        JobReq jobReq = new JobReq("Cruise Ship Attendant", 1L);
        try {
            businessJobService.createNewJob(jobReq, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND_COMPANY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void createNewJob_whenJobNameIsExisted_thenReturnJobIsExistedResp() {
        JobReq jobReq = new JobReq("Cruise Ship Attendant", 1L);
        Company foundCompany = new Company(1L);
        List<Job> existedJobs = new ArrayList<>();
        existedJobs.add(new Job("Cruise Ship Attendant", 1L));

        Mockito.when(companyService.findById(1L)).thenReturn(foundCompany);
        Mockito.when(jobService.checkJobExistedInSystem(jobReq.getCompanyId(), jobReq.getJobName().toLowerCase())).thenReturn(existedJobs);
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        BaseResp expectedResult = new BaseResp<>(new JobDTO(existedJobs.get(0), ""));
        Assert.assertEquals(expectedResult.getCode(), businessJobService.createNewJob(jobReq, authentication).getCode());
    }

    @Test
    public void createNewJob_whenSaveJobFailed_thenReturnSaveFailError() {
        JobReq jobReq = new JobReq("Cruise Ship Attendant", 1L);
        Company foundCompany = new Company(1L);
        Mockito.when(companyService.findById(1L)).thenReturn(foundCompany);
        Mockito.when(jobService.save(jobReq.convertToJob())).thenReturn(null);
        try {
            businessJobService.createNewJob(jobReq, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_FAIL.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void createNewJob_whenSaveSuccess_thenReturnSuccess() {
        JobReq jobReq = new JobReq("Cruise Ship Attendant", 1L);
        Job job = jobReq.convertToJob();
        Company foundCompany = new Company(1L);
        Mockito.when(companyService.findById(1L)).thenReturn(foundCompany);
        Mockito.when(jobService.save(job)).thenReturn(job);
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessJobService.createNewJob(jobReq, authentication).getCode());
    }
}
