package com.qooco.boost.controllers;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/11/2018 - 5:09 PM
 */

import com.qooco.boost.business.BusinessJobService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.JobDTO;
import com.qooco.boost.models.request.JobReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(URLConstants.JOB_PATH)
@Api(tags = "Jobs",value = URLConstants.JOB_PATH, description = "Job Controller")
public class JobController extends BaseController {
    @Autowired
    private BusinessJobService businessJobService;

    @ApiOperation(value = "Get Job Title", httpMethod = "GET", response = JobListResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = URLConstants.GET_METHOD, method = RequestMethod.GET)
    public Object getGlobalJobs(Authentication authentication) {
        BaseResp jobsResp = businessJobService.getGlobalJobs(authentication);
        return success(jobsResp);
    }

    @ApiOperation(value = "Get Job by Company", httpMethod = "GET", response = JobListResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = URLConstants.BY_COMPANY_PATH, method = RequestMethod.GET)
    public Object getJobsByCompany(@RequestParam(value = "id") Long id, Authentication authentication) {
        BaseResp jobsResp = businessJobService.getJobsByCompany(id, authentication);
        return success(jobsResp);
    }

    @ApiOperation(value = "Create New Job", httpMethod = "POST", response = JobResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.JOB_IS_EMPTY + " : " + StatusConstants.JOB_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.JOB_NAME_IS_EMPTY + " : " + StatusConstants.JOB_NAME_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.JOB_NAME_IS_TOO_SHORT + " : " + StatusConstants.JOB_NAME_IS_TOO_SHORT_MESSAGE
                    + "<br>" + StatusConstants.JOB_NAME_IS_TOO_LONG + " : " + StatusConstants.JOB_NAME_IS_TOO_LONG_MESSAGE
                    + "<br>" + StatusConstants.JOB_DESCRIPTION_IS_TOO_SHORT + " : " + StatusConstants.JOB_DESCRIPTION_IS_TOO_SHORT_MESSAGE
                    + "<br>" + StatusConstants.JOB_DESCRIPTION_IS_TOO_LONG + " : " + StatusConstants.JOB_DESCRIPTION_IS_TOO_LONG_MESSAGE
                    + "<br>" + StatusConstants.COMPANY_ID_IS_EMPTY + " : " + StatusConstants.COMPANY_ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.SAVE_FAIL + " : " + StatusConstants.SAVE_FAIL_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = URLConstants.SAVE_METHOD)
    public Object createNewJob(@RequestBody final JobReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
		BaseResp jobResp = businessJobService.createNewJob(request, authentication);
        return success(jobResp);
    }

    class JobListResp extends BaseResp<List<JobDTO>> {}

    class JobResp extends BaseResp<JobDTO> {}
}
