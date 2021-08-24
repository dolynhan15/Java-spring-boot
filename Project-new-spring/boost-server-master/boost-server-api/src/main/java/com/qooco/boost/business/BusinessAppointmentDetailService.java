package com.qooco.boost.business;

import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.appointment.AppointmentEventIdRemoveReq;
import com.qooco.boost.models.request.appointment.AppointmentEventRemoveReq;
import com.qooco.boost.models.request.appointment.EventTimeReq;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface BusinessAppointmentDetailService {
    List<AppointmentDetail> getAppointmentDetailOfCandidate(Authentication authentication, List<Integer> statuses, Date startDate, Date endDate);

    BaseResp getAppointmentDetailOfCandidate(Authentication authentication, Long startDate, Long endDate, int page, int size);

    BaseResp getAppointmentDetailForReminder(Authentication authentication, Long companyId, long lastTime, int size);

    BaseResp getAppointmentDetailOfCompanyInDuration(Authentication authentication, long companyId, long managerId, long startDate, long endDate);

    BaseResp getAllAppointmentDetailInDurationForProfile(Authentication authentication, long appointmentId, long startDate, long endDate);

    BaseResp getAppointmentEventCounting(long staffId, long companyId, long startDate, Long endDate, String timeZone, Authentication authentication);

    BaseResp getAppointmentEventCountingByUserInCompany(long companyId, long startDate, Long endDate, Authentication authentication);

    BaseResp getAppointmentEventInDurationUserInCompany(long companyId, long startDate, long endDate, Authentication authentication);

    @Transactional
    BaseResp saveAppointmentEvent(Long eventId, EventTimeReq request, Authentication authentication);

    BaseResp deleteAppointmentDetails(long[] ids, Authentication authentication);

    BaseResp deleteAppointmentDetails(Long id, long fromDate, long toDate, Authentication authentication);

    BaseResp deleteAppointmentDetailsWithNewOrChangeOption(AppointmentEventRemoveReq request, Authentication authentication);

    BaseResp deleteAppointmentDetailIdsWithNewOrChangeOption(AppointmentEventIdRemoveReq request, Authentication authentication);

    List<AppointmentDetail> cancelAppointmentDetails(List<AppointmentDetail> appointmentDetails);

    BaseResp hasExpiredEvent(Authentication authentication);

    BaseResp getCandidatesOfExpiredEventsForFeedback(Long appointmentId, List<String> decideLaterCandidates, int size, Authentication authentication);
}
