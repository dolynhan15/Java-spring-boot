package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessStatisticService;
import com.qooco.boost.constants.PaginationConstants;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.statistic.CompanyDashboardDTO;
import com.qooco.boost.models.dto.statistic.EmployeeDashboardDTO;
import com.qooco.boost.models.dto.statistic.EmployeeDashboardDetailDTO;
import com.qooco.boost.models.request.DurationRequest;
import com.qooco.boost.models.response.StatisticResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(URLConstants.STATISTIC_PATH)
@Api(tags = "Statistic", value = URLConstants.STATISTIC_PATH, description = "Statistic Controller")
public class StatisticController extends BaseController {
    @Autowired
    private BusinessStatisticService businessStatisticService;

    @ApiOperation(value = "Save view profile in statistic", httpMethod = "PUT", response = ResponseStatus.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.CAN_NOT_VIEW_YOURSELF + " : " + StatusConstants.CAN_NOT_VIEW_YOURSELF_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_CURRICULUM_VITAE + " : " + StatusConstants.NOT_FOUND_USER_CURRICULUM_VITAE_MESSAGE
                    + "<br>" + StatusConstants.CANDIDATE_IS_ALREADY_APPLIED + " : " + StatusConstants.CANDIDATE_IS_ALREADY_APPLIED_MESSAGE
                    + "<br>" + StatusConstants.CANDIDATE_IS_ALREADY_REJECTED + " : " + StatusConstants.CANDIDATE_IS_ALREADY_REJECTED_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = URLConstants.VIEW_PROFILE, method = RequestMethod.PATCH)
    @PutMapping(value = URLConstants.VIEW_PROFILE)
    public Object saveViewProfile(@RequestParam(value = "candidateId") Long candidateId,
                                  @RequestParam(value = "vacancyId") Long vacancyId,
                                  Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessStatisticService.saveViewProfile(candidateId, vacancyId, authenticatedUser.getId());
        return success(result);
    }

    @ApiOperation(value = "Get statistic of user", httpMethod = "GET", response = StatisticCountResp.class, notes = GET_NOTE)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = URLConstants.GET_METHOD, method = RequestMethod.GET)
    public Object getStatistic(@RequestParam(value = "timeZone", required = false, defaultValue = "UTC") String timeZone, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessStatisticService.getStatisticByUserProfileId(timeZone, authenticatedUser.getId());
        return success(result);
    }

    //===================================== Company And Employee Dashboard==========================================================================
    @ApiOperation(value = "The company dashboard about vacancy seat with duration time", httpMethod = "GET", response = CompanyDashboardResp.class, notes = GET_NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = URLConstants.COMPANY_DASHBOARD)
    public Object getCompanyDashboard(@Valid DurationRequest durationRequest, Authentication authentication) {
        BaseResp result = businessStatisticService.getCompanyDashboard(durationRequest, authentication);
        return success(result);
    }

    @ApiOperation(value = "The employee dashboard about vacancy seat, appointments, candidates processed and active time with duration time", httpMethod = "GET", response = EmployeeDashboardResp.class, notes = GET_NOTE_EMPLOYEE_DASHBOARD)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = URLConstants.EMPLOYEE_DASHBOARD)
    public Object getEmployeeDashboard(@Valid DurationRequest durationRequest, Authentication authentication,
                                       @RequestParam(value = "page", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) int page,
                                       @RequestParam(value = "size", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        BaseResp result = businessStatisticService.getEmployeeDashboard(durationRequest, page, size, authentication);
        return success(result);
    }

    @ApiOperation(value = "The employee dashboard detail with duration time. Need to input staffId in the path", httpMethod = "GET", response = EmployeeDashboardDetailResp.class, notes = GET_NOTE_EMPLOYEE_DETAIL)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = URLConstants.EMPLOYEE_DASHBOARD + URLConstants.ID)
    public Object getEmployeeDashboardDetail(@PathVariable("id") Long staffId, @Valid DurationRequest durationRequest, Authentication authentication) {
        BaseResp result = businessStatisticService.getEmployeeDashboardDetail(staffId, durationRequest, authentication);
        return success(result);
    }


    private static final String GET_NOTE = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
            + "<br>" + StatusConstants.INVALID_TIME_RANGE + " : " + StatusConstants.INVALID_TIME_RANGE_MESSAGE
            + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
            ;

    private static final String GET_NOTE_EMPLOYEE_DASHBOARD = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
            + "<br>" + StatusConstants.INVALID_TIME_RANGE + " : " + StatusConstants.INVALID_TIME_RANGE_MESSAGE
            + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
            + "<br>" + StatusConstants.INVALID_PAGINATION + " : " + StatusConstants.INVALID_PAGINATION_MESSAGE

            ;

    private static final String GET_NOTE_EMPLOYEE_DETAIL = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
            + "<br>" + StatusConstants.INVALID_TIME_RANGE + " : " + StatusConstants.INVALID_TIME_RANGE_MESSAGE
            + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_STAFF + " : " + StatusConstants.NOT_FOUND_STAFF_MESSAGE
            ;

    class StatisticCountResp extends BaseResp<StatisticResp> {
    }

    class CompanyDashboardResp extends BaseResp<CompanyDashboardDTO> {
    }
    class EmployeeDashboardResp extends BaseResp<PagedResult<EmployeeDashboardDTO>> {
    }
    class EmployeeDashboardDetailResp extends BaseResp<EmployeeDashboardDetailDTO> {
    }


}
