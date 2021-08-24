package com.qooco.boost.business;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/11/2018 - 6:00 PM
 */

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.JobReq;
import org.springframework.security.core.Authentication;

public interface BusinessJobService extends BaseBusinessService{
    BaseResp getGlobalJobs(Authentication authentication);

    BaseResp getJobsByCompany(Long companyId, Authentication authentication);

    BaseResp createNewJob(JobReq job, Authentication authentication);
}
