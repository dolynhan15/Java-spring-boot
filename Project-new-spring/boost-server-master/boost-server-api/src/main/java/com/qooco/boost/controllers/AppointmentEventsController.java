package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessAppointmentDetailService;
import com.qooco.boost.constants.PaginationConstants;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.appointment.*;
import com.qooco.boost.models.request.appointment.AppointmentEventIdRemoveReq;
import com.qooco.boost.models.request.appointment.AppointmentEventRemoveReq;
import com.qooco.boost.models.request.appointment.EventTimeReq;
import com.qooco.boost.models.response.CandidateFeedbackResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Appointment Events", value = URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS, description = "Appointment Events management")
@RestController
@RequestMapping()
public class AppointmentEventsController extends BaseController {
    @Autowired
    private BusinessAppointmentDetailService businessAppointmentDetailService;

    @ApiOperation(value = "Count number appointment event of a manager", httpMethod = "GET", response = AppointmentEventCountDTO.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.USER_PROFILE_ID_NOT_EXIST_CODE + " : " + StatusConstants.USER_PROFILE_ID_NOT_EXIST_MESSAGE
                    + "<br>" + StatusConstants.TIME_ZONE_INVALID + " : " + StatusConstants.TIME_ZONE_INVALID_MESSAGE
                    + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS + URLConstants.COUNT)
    public Object getAppointmentEventCount(@RequestParam(value = "staffId") long staffId,
                                           @RequestParam(value = "companyId") long companyId,
                                           @RequestParam(value = "startDate") long startDate,
                                           @RequestParam(value = "endDate", required = false) Long endDate,
                                           @RequestParam(value = "timeZone") String timeZone,
                                           Authentication authentication) {
        BaseResp result = businessAppointmentDetailService.getAppointmentEventCounting(staffId, companyId, startDate, endDate, timeZone, authentication);
        return success(result);
    }

    @ApiOperation(value = "Cancel appointment events in appointment and move all events to another or new, not delete the appointment", httpMethod = "POST", response = BaseResp.class, notes = deleteAppointmentDetailsWithDurationNotes)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS + URLConstants.MOVE_FROM_DURATION)
    public Object deleteAppointmentEventWithNewOrChangeOption(@RequestBody AppointmentEventRemoveReq request,
                                                              Authentication authentication) {
        BaseResp result = businessAppointmentDetailService.deleteAppointmentDetailsWithNewOrChangeOption(request, authentication);
        return success(result);
    }

    @ApiOperation(value = "Select Slot From Profile Application", httpMethod = "PUT", response = AppointmentDetailResp.class, notes = appointmentDetailNotes)
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS + URLConstants.ID_PATH)
    @PutMapping(URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS + URLConstants.ID_PATH)
    public Object saveAppointmentEvent(@PathVariable Long id, @RequestBody EventTimeReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        BaseResp result = businessAppointmentDetailService.saveAppointmentEvent(id, request, authentication);
        return success(result);
    }

    @ApiOperation(value = "Get an appointment detail of staff in company in duration", httpMethod = "GET", response = AppointmentOfCompanyResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.INVALID_TIME_RANGE + " : " + StatusConstants.INVALID_TIME_RANGE_MESSAGE
                    + "<br>" + StatusConstants.STAFF_OF_ANOTHER_COMPANY + " : " + StatusConstants.STAFF_OF_ANOTHER_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_STAFF + " : " + StatusConstants.NOT_FOUND_STAFF_MESSAGE
                    + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS)
    public Object getAppointmentDetailOfCompanyInDuration(Authentication authentication,
                                                          @RequestParam(value = "companyId") long companyId,
                                                          @RequestParam(value = "staffId") long staffId,
                                                          @RequestParam(value = "startDate") long startDate,
                                                          @RequestParam(value = "endDate") long endDate) {
        BaseResp result = businessAppointmentDetailService.getAppointmentDetailOfCompanyInDuration(authentication, companyId, staffId, startDate, endDate);
        return success(result);
    }

    @ApiOperation(value = "Delete list events", httpMethod = "DELETE", response = BaseResp.class, notes = deleteAppointmentDetailIdsNotes)
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS)
    public Object deleteAppointmentDetails(Authentication authentication, @RequestParam(value = "id") long[] ids) {
        BaseResp result = businessAppointmentDetailService.deleteAppointmentDetails(ids, authentication);
        return success(result);
    }

    @ApiOperation(value = "Cancel appointment event ids in appointment and move all events to another or new, not delete the appointment", httpMethod = "POST", response = BaseResp.class, notes = deleteAppointmentDetailsWithDurationNotes)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS + URLConstants.MOVE)
    public Object deleteAppointmentEventIdsWithNewOrChangeOption(@RequestBody AppointmentEventIdRemoveReq request,
                                                                 Authentication authentication) {
        BaseResp result = businessAppointmentDetailService.deleteAppointmentDetailIdsWithNewOrChangeOption(request, authentication);
        return success(result);
    }

    @ApiOperation(value = "Count number appointment event of a fit user in company", httpMethod = "GET", response = AppointmentEventCountDTO.class,
            notes = countAppointmentDetail
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS + URLConstants.CALENDAR)
    public Object getAppointmentEventCount(@RequestParam(value = "companyId") long companyId,
                                           @RequestParam(value = "startDate") long startDate,
                                           @RequestParam(value = "endDate", required = false) Long endDate,
                                           Authentication authentication) {
        BaseResp result = businessAppointmentDetailService.getAppointmentEventCountingByUserInCompany(
                companyId, startDate, endDate, authentication);
        return success(result);
    }

    @ApiOperation(value = "Get all event for reminder of user (fit or profile application", httpMethod = "GET", response = AppointmentDetailsResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS + URLConstants.REMIND)
    public Object getReminderEvents(Authentication authentication,
                                    @RequestParam(value = "companyId", required = false) Long companyId,
                                    @RequestParam(value = "lastTime", defaultValue = PaginationConstants.DEFAULT_LAST_TIME) long lastTime,
                                    @RequestParam(value = "size", defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        BaseResp result = businessAppointmentDetailService.getAppointmentDetailForReminder(authentication, companyId, lastTime, size);
        return success(result);
    }

    @ApiOperation(value = "Count number appointment event of a fit user in company", httpMethod = "GET", response = AppointmentsResp.class,
            notes = countAppointmentDetail
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS + URLConstants.CALENDAR + URLConstants.DURATION)
    public Object getAppointmentEventInDuration(@RequestParam(value = "companyId") long companyId,
                                                @RequestParam(value = "startDate") long startDate,
                                                @RequestParam(value = "endDate") long endDate,
                                                Authentication authentication) {
        BaseResp result = businessAppointmentDetailService.getAppointmentEventInDurationUserInCompany(
                companyId, startDate, endDate, authentication);
        return success(result);
    }

    @ApiOperation(value = "Has expired events of a manager", httpMethod = "GET", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS + URLConstants.HAS_EXPIRED)
    public Object hasExpiredEvent(Authentication authentication) {
        BaseResp result = businessAppointmentDetailService.hasExpiredEvent(authentication);
        return success(result);
    }

    @ApiOperation(value = "Get candidates of expired events for feedback", httpMethod = "GET", response = CandidateResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH + URLConstants.EVENTS + URLConstants.EXPIRED + URLConstants.CANDIDATE_OF_VACANCY)
    public Object getCandidatesOfExpiredEventsForFeedback(@RequestParam(value = "appointmentId", required = false) Long appointmentId,
                                                          @RequestParam(value = "decideLaterCandidate", required = false) List<String> decideLaterCandidates,
                                                          @RequestParam(value = "size", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
                                                          Authentication authentication) {
        BaseResp result = businessAppointmentDetailService.getCandidatesOfExpiredEventsForFeedback(appointmentId, decideLaterCandidates, size, authentication);
        return success(result);
    }

    /**
     * ==================== To show swagger ===============
     **/
    private final String deleteAppointmentDetailsWithDurationNotes = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_APPOINTMENT + " : " + StatusConstants.NOT_FOUND_APPOINTMENT_MESSAGE
            + "<br>" + StatusConstants.CURRENT_DATE_TIME_IS_OVER + " : " + StatusConstants.CURRENT_DATE_TIME_IS_OVER_MESSAGE
            + "<br>" + StatusConstants.INVALID_TIME_RANGE + " : " + StatusConstants.INVALID_TIME_RANGE_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS_APPOINTMENT + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_APPOINTMENT_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_APPOINTMENT_DETAIL + " : " + StatusConstants.NOT_FOUND_APPOINTMENT_DETAIL;

    private final String appointmentDetailNotes = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
            + "<br>" + StatusConstants.APPOINTMENTS_EVENT_CONFLICT + " : " + StatusConstants.APPOINTMENTS_EVENT_CONFLICT_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_USER_CURRICULUM_VITAE + " : " + StatusConstants.NOT_FOUND_USER_CURRICULUM_VITAE_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_APPOINTMENT_DETAIL + " : " + StatusConstants.NOT_FOUND_APPOINTMENT_DETAIL_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
            + "<br>" + StatusConstants.VACANCY_IS_INACTIVE + " : " + StatusConstants.VACANCY_IS_INACTIVE_MESSAGE
            + "<br>" + StatusConstants.VACANCY_IS_SUSPENDED + " : " + StatusConstants.VACANCY_IS_SUSPENDED_MESSAGE
            + "<br>" + StatusConstants.APPOINTMENTS_SELECTED_DATE_IN_SUSPENDED_TIME + " : " + StatusConstants.APPOINTMENTS_SELECTED_DATE_IN_SUSPENDED_TIME_MESSAGE
            + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE;

    private final String deleteAppointmentDetailIdsNotes = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_APPOINTMENT + " : " + StatusConstants.NOT_FOUND_APPOINTMENT_MESSAGE
            + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_APPOINTMENT_DETAIL + " : " + StatusConstants.NOT_FOUND_APPOINTMENT_DETAIL_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS_APPOINTMENT + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_APPOINTMENT_MESSAGE
            + "<br>" + StatusConstants.INVALID_EVENT_ACTION + " : " + StatusConstants.INVALID_EVENT_ACTION_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
            + "<br>" + StatusConstants.APPOINTMENT_IS_EXPIRED + " : " + StatusConstants.APPOINTMENT_IS_EXPIRED_MESSAGE;

    private final String countAppointmentDetail = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.USER_PROFILE_ID_NOT_EXIST_CODE + " : " + StatusConstants.USER_PROFILE_ID_NOT_EXIST_MESSAGE
            + "<br>" + StatusConstants.TIME_ZONE_INVALID + " : " + StatusConstants.TIME_ZONE_INVALID_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS_APPOINTMENT + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_APPOINTMENT_MESSAGE;

    class AppointmentsResp extends BaseResp<List<AppointmentDTO>> {
    }

    class AppointmentDetailResp extends BaseResp<AppointmentDetailDTO> {
    }

    class AppointmentOfCompanyResp extends BaseResp<List<AppointmentShortVacancyDTO>> {
    }

    class AppointmentDetailsResp extends BaseResp<List<AppointmentDetailFullDTO>> {
    }

    class CandidateResp extends BaseResp<PagedResult<CandidateFeedbackResp>> {
    }
}
