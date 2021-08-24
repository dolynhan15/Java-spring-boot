package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessAppointmentDetailService;
import com.qooco.boost.business.BusinessAppointmentService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.appointment.AppointmentDTO;
import com.qooco.boost.models.dto.appointment.AppointmentDetailDTO;
import com.qooco.boost.models.dto.appointment.AppointmentReminderDTO;
import com.qooco.boost.models.dto.appointment.CandidateEventDTO;
import com.qooco.boost.models.request.PageRequest;
import com.qooco.boost.models.request.appointment.AppointmentCandidate;
import com.qooco.boost.models.request.appointment.AppointmentRemoveReq;
import com.qooco.boost.models.request.appointment.AppointmentReq;
import com.qooco.boost.models.user.UserIdReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Api(tags = "Appointment", value = URLConstants.APPOINTMENT_PATH, description = "Appointment management")
@RestController
@RequestMapping()
public class AppointmentController extends BaseController {

    @Autowired
    private BusinessAppointmentService businessAppointmentService;
    @Autowired
    private BusinessAppointmentDetailService businessAppointmentDetailService;

    @ApiOperation(value = "Delete an appointment and cancel all events", httpMethod = "DELETE", response = BaseResp.class, notes = deleteNotes)
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(URLConstants.APPOINTMENT_PATH + URLConstants.ID_PATH)
    public Object deleteAppointment(@PathVariable Long id, Authentication authentication) {
        BaseResp result = businessAppointmentService.deleteAppointment(id, authentication);
        return success(result);
    }

    @ApiOperation(value = "Cancel appointment events in appointment, not delete the appointment", httpMethod = "DELETE", response = BaseResp.class, notes = deleteAppointmentDetailsWithDurationNotes)
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(URLConstants.APPOINTMENT_PATH + URLConstants.ID_PATH + URLConstants.EVENTS)
    public Object deleteAppointmentEvent(@PathVariable Long id,
                                         @RequestParam(value = "fromDate") long fromDate,
                                         @RequestParam(value = "toDate") long toDate,
                                         Authentication authentication) {
        BaseResp result = businessAppointmentDetailService.deleteAppointmentDetails(id, fromDate, toDate, authentication);
        return success(result);
    }

    @Deprecated
    @ApiOperation(value = "Delete an appointment and move all events to another or new", httpMethod = "POST", response = BaseResp.class, notes = deleteNotes)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.APPOINTMENT_PATH + URLConstants.REMOVE)
    public Object deleteAppointmentWithNewOrChangeOption(@RequestBody AppointmentRemoveReq request, Authentication authentication) {
        BaseResp result = businessAppointmentService.deleteAppointmentWithNewOrChangeOption(request, authentication);
        return success(result);
    }

    @ApiOperation(value = "Delete an appointment and move all events to another or new", httpMethod = "POST", response = BaseResp.class, notes = deleteNotes)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.APPOINTMENT_PATH + URLConstants.MOVE)
    public Object deleteAppointmentWithNewOrChangeOptionNew(@RequestBody AppointmentRemoveReq request, Authentication authentication) {
        BaseResp result = businessAppointmentService.deleteAppointmentWithNewOrChangeOption(request, authentication);
        return success(result);
    }

    @ApiOperation(value = "Get list appointment for a vacancy or a company", httpMethod = "GET", response = AppointmentsResp.class, notes = notes)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH)
    public Object getAppointmentByVacancyIdOrCompanyId(@RequestParam(value = "vacancyId", required = false) Long vacancyId,
                                                       @RequestParam(value = "companyId", required = false) Long companyId,
                                                       @RequestParam(value = "isActive", required = false) boolean isActive,
                                                       @RequestParam(value = "exceptedUserCVId", required = false) Long exceptedUserCVId,
                                                       Authentication authentication) {
        BaseResp result = businessAppointmentService.getAppointmentByVacancyIdOrCompanyId(vacancyId, companyId, isActive, authentication, exceptedUserCVId);
        return success(result);
    }

    @ApiOperation(value = "Get appointments management with param isCurrent", httpMethod = "GET", response = AppointmentsRespPage.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE
                    + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_INACTIVE + " : " + StatusConstants.VACANCY_IS_INACTIVE_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_SUSPENDED + " : " + StatusConstants.VACANCY_IS_SUSPENDED_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH + URLConstants.MANAGE)
    public Object getAppointmentsInManagement(@RequestParam(value = "vacancyId") Long vacancyId,
                                              @Valid PageRequest pageRequest,
                                              @RequestParam(value = "isCurrent", required = false) boolean isCurrent,
                                              Authentication authentication) {
        Long userProfileId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        BaseResp result = businessAppointmentService.getAppointmentsInManagement(vacancyId, userProfileId, pageRequest.getPage(), pageRequest.getSize(), isCurrent, authentication);
        return success(result);
    }

    @ApiOperation(value = "Get an appointment", httpMethod = "GET", response = AppointmentResp.class, notes = notes)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH + URLConstants.ID_PATH)
    public Object getAppointment(@PathVariable Long id, Authentication authentication) {
        BaseResp result = businessAppointmentService.getAppointment(id, authentication);
        return success(result);
    }

    @ApiOperation(value = "Get an appointment detail of staff in company in duration for Profile application base on appointment Id", httpMethod = "GET",
            response = AppointmentDetailProfileResp.class, notes = appointmentDetailNotes)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH + URLConstants.ID_PATH + URLConstants.PROFILE_EVENTS + URLConstants.TIME)
    public Object getAllAppointmentEventForProfile(@PathVariable Long id,
                                                   @RequestParam(value = "startDate") long startDate,
                                                   @RequestParam(value = "endDate") long endDate,
                                                   Authentication authentication) {
        BaseResp result = businessAppointmentDetailService.getAllAppointmentDetailInDurationForProfile(authentication, id, startDate, endDate);
        return success(result);
    }

    @ApiOperation(value = "Count the latest appointment reminder", httpMethod = "GET", response = AppointmentReminderResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH + URLConstants.COUNT)
    public Object countLatestAppointment(@RequestParam(value = "companyId", required = false) Long companyId, Authentication authentication) {
        BaseResp result = businessAppointmentService.countLatestAppointment(companyId, authentication);
        return success(result);
    }

    @ApiOperation(value = "Create an appointment for a vacancy - Version 2", httpMethod = "POST", response = AppointmentResp.class, notes = notes)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.VERSION_2 + URLConstants.APPOINTMENT_PATH)
    public Object saveAppointmentV2(@RequestBody AppointmentReq appointmentReq, Authentication authentication) {
        saveRequestBodyToSystemLogger(appointmentReq);
        BaseResp result = businessAppointmentService.saveAppointmentV2(null, appointmentReq, authentication);
        return success(result);
    }

    @ApiOperation(value = "Update an appointment for a vacancy - Version 2", httpMethod = "PUT", response = AppointmentResp.class, notes = notes)
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.VERSION_2 + URLConstants.APPOINTMENT_PATH + URLConstants.ID_PATH)
    @PutMapping(URLConstants.VERSION_2 + URLConstants.APPOINTMENT_PATH + URLConstants.ID_PATH)
    public Object updateAppointmentV2(@PathVariable Long id, @RequestBody AppointmentReq appointmentReq, Authentication authentication) {
        saveRequestBodyToSystemLogger(appointmentReq);
        appointmentReq.setId(id);
        BaseResp result = businessAppointmentService.saveAppointmentV2(id, appointmentReq, authentication);
        return success(result);
    }

    @ApiOperation(value = "Save candidate appointment v2 (Assign candidate to an appointment)", httpMethod = "POST", response = AppointmentDetailResp.class, notes = assignAppointmentDetailNotesV2)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.VERSION_2 + URLConstants.APPOINTMENT_PATH + URLConstants.ID_PATH + URLConstants.EVENTS)
    public Object saveAppointmentEventV2(@PathVariable Long id, @RequestBody UserIdReq userCvIdReq, Authentication authentication) {
        AppointmentCandidate appointmentCandidate = new AppointmentCandidate(id, userCvIdReq.getUserCVId());
        saveRequestBodyToSystemLogger(appointmentCandidate);
        BaseResp result = businessAppointmentService.assignCandidateToAppointment(id, userCvIdReq.getUserCVId(), authentication);
        return success(result);
    }

    @ApiOperation(value = "Get events of candidate by start date and end date", httpMethod = "GET", response = PersonalAppointmentDetailResp.class, notes = getAppointmentDetailsNotes)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APPOINTMENT_PATH + URLConstants.PROFILE_EVENTS)
    public Object getAppointmentDetailOfCandidate(
            @Valid PageRequest request,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            Authentication authentication) {
        BaseResp result = businessAppointmentDetailService.getAppointmentDetailOfCandidate(authentication, startDate, endDate, request.getPage(), request.getSize());
        return success(result);
    }

    /**
     * ==================== To show swagger ===============
     **/
    private final String notes = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE
            + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS_VACANCY + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_VACANCY_MESSAGE
            + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
            + "<br>" + StatusConstants.COMPANY_ID_IS_EMPTY + " : " + StatusConstants.COMPANY_ID_IS_EMPTY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_LOCATION + " : " + StatusConstants.NOT_FOUND_LOCATION_MESSAGE
            + "<br>" + StatusConstants.APPOINTMENT_DATE_TIME_REQUIRED + " : " + StatusConstants.APPOINTMENT_DATE_TIME_REQUIRED_MESSAGE
            + "<br>" + StatusConstants.START_DATE_RANGE_IS_PAST + " : " + StatusConstants.START_DATE_RANGE_IS_PAST_MESSAGE
            + "<br>" + StatusConstants.APPOINTMENTS_IS_DUPLICATE + " : " + StatusConstants.APPOINTMENTS_IS_DUPLICATE_MESSAGE
            + "<br>" + StatusConstants.APPOINTMENTS_NOT_IN_VACANCY + " : " + StatusConstants.APPOINTMENTS_NOT_IN_VACANCY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_APPOINTMENT + " : " + StatusConstants.NOT_FOUND_APPOINTMENT_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_ROLE + " : " + StatusConstants.NOT_FOUND_ROLE_MESSAGE
            + "<br>" + StatusConstants.VACANCY_IS_INACTIVE + " : " + StatusConstants.VACANCY_IS_INACTIVE_MESSAGE
            + "<br>" + StatusConstants.VACANCY_IS_SUSPENDED + " : " + StatusConstants.VACANCY_IS_SUSPENDED_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE;

    private final String deleteNotes = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE;

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

    private final String assignAppointmentDetailNotesV2 = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS_APPOINTMENT + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_APPOINTMENT_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_USER_CURRICULUM_VITAE + " : " + StatusConstants.NOT_FOUND_USER_CURRICULUM_VITAE_MESSAGE
            + "<br>" + StatusConstants.APPOINTMENT_INVITATION_IS_SENT_ALREADY + " : " + StatusConstants.APPOINTMENT_INVITATION_IS_SENT_ALREADY_MESSAGE
            + "<br>" + StatusConstants.APPOINTMENT_IS_EXPIRED + " : " + StatusConstants.APPOINTMENT_IS_EXPIRED_MESSAGE
            + "<br>" + StatusConstants.APPOINTMENT_IS_NOT_AVAILABLE + " : " + StatusConstants.APPOINTMENT_IS_NOT_AVAILABLE_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_APPOINTMENT + " : " + StatusConstants.NOT_FOUND_APPOINTMENT_MESSAGE;

    private final String deleteAppointmentDetailsWithDurationNotes = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_APPOINTMENT + " : " + StatusConstants.NOT_FOUND_APPOINTMENT_MESSAGE
            + "<br>" + StatusConstants.CURRENT_DATE_TIME_IS_OVER + " : " + StatusConstants.CURRENT_DATE_TIME_IS_OVER_MESSAGE
            + "<br>" + StatusConstants.INVALID_TIME_RANGE + " : " + StatusConstants.INVALID_TIME_RANGE_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS_APPOINTMENT + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_APPOINTMENT_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_APPOINTMENT_DETAIL + " : " + StatusConstants.NOT_FOUND_APPOINTMENT_DETAIL;

    private final String getAppointmentDetailsNotes = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.START_DATE_IS_REQUIRED + " : " + StatusConstants.START_DATE_IS_REQUIRED_MESSAGE
            + "<br>" + StatusConstants.START_DATE_AFTER_END_DATE + " : " + StatusConstants.START_DATE_AFTER_END_DATE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
            + "<br> status: Acce" + StatusConstants.START_DATE_AFTER_END_DATE + " : " + StatusConstants.START_DATE_AFTER_END_DATE;

    private class AppointmentResp extends BaseResp<AppointmentDTO> {
    }

    private class AppointmentsResp extends BaseResp<List<AppointmentDTO>> {
    }

    private class AppointmentsRespPage extends BaseResp<PagedResult<AppointmentDTO>> {
    }

    private class AppointmentReminderResp extends BaseResp<AppointmentReminderDTO> {
    }

    private class AppointmentDetailResp extends BaseResp<AppointmentDetailDTO> {
    }

    private class PersonalAppointmentDetailResp extends BaseResp<PagedResult<CandidateEventDTO>> {
    }

    private class AppointmentDetailProfileResp extends BaseResp<List<Date>> {
    }
}
