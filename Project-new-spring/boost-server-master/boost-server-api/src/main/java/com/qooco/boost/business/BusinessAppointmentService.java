package com.qooco.boost.business;

import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.appointment.AppointmentBaseReq;
import com.qooco.boost.models.request.appointment.AppointmentRemoveReq;
import com.qooco.boost.models.request.appointment.AppointmentReq;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface BusinessAppointmentService {

    BaseResp getAppointment(Long id, Authentication authentication);

    BaseResp getAppointmentsByVacancyId(Long vacancyId, boolean isActive, Authentication authentication, Long exceptedUserCVId);

    BaseResp getAppointmentsByCompanyId(Long companyId, boolean isActive, Authentication authentication, Long exceptedUserCVId);

    BaseResp getAppointmentByVacancyIdOrCompanyId(Long vacancyId, Long companyId, boolean isOnlyActive, Authentication authentication, Long exceptedUserCVId);

    BaseResp getAppointmentsInManagement(Long vacancyId, Long userProfileId, int page, int size, boolean isCurrent, Authentication authentication);

    BaseResp saveAppointmentV2(Long id, AppointmentReq appointmentReq, Authentication authentication);

    List<Appointment> validateAppointmentRequests(List<? extends AppointmentBaseReq> appointmentReqs, Company company, Authentication authentication);

    BaseResp countLatestAppointment(Long companyId, Authentication authentication);

    BaseResp assignCandidateToAppointment(long id, long userCvId, Authentication authentication);

    BaseResp deleteAppointment(Long id, Authentication authentication);

    BaseResp deleteAppointments(List<Long> ids, Authentication authentication);

    void assignAppointmentToNewManagerAfterChangeRole(Staff oldManager, Staff newManager, Authentication authentication);

    BaseResp deleteAppointmentWithNewOrChangeOption(AppointmentRemoveReq request, Authentication authentication);

    List<Appointment> findAppointmentInSuspendedRange(Vacancy vacancy);
}
