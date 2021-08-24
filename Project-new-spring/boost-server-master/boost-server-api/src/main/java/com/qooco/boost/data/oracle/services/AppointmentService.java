package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.model.VacancyHasAppointment;
import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.Vacancy;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface AppointmentService {

    Appointment findById(Long id);

    Appointment findValidById(Long id);

    List<Appointment> findById(Long vacancyId, Long managerId, Long locationId, Date appointmentDate);

    Appointment save(Appointment appointment);

    List<Appointment> save(List<Appointment> appointments);

    List<Appointment> findNotExpiredByVacancyId(Long vacancyId);

    int updateDeleteStatus(Long id, boolean isDelete, Long updatedBy, Date updatedDate);

    int updateDeleteStatus(List<Long> ids, boolean isDelete, Long updatedBy, Date updatedDate);

    long countValid(List<Long> ids);

    List<Appointment> findNotExpiredByCompanyIdAndUserProfileId(Long companyId, Long userProfileId);

    List<Appointment> findNotExpiredByCompanyId(Long companyId);

    List<Appointment> findNotExpiredByVacancyIdAndUserProfileId(Long vacancyId, Long userProfileId);

    List<Appointment> findByVacancyId(Long vacancyId);

    List<Appointment> findByOpeningVacancyId(Long vacancyId);

    List<Appointment> findByVacancyIdAndUserProfileId(Long vacancyId, Long userProfileId);

    List<Appointment> findByCompanyIdAndUserProfileId(Long companyId, Long userProfileId);

    List<Appointment> findByCompanyId(Long companyId);

    List<Long> findByStaff(Long staffId);

    List<Appointment> findAvailableByIdsAndAppointmentDetailStatus(List<Long> ids, List<Integer> appointmentDetailStatus);

    Page<Appointment> findCurrentByVacancyAndUserAndStatuses(Long vacancyId, Long userProfileId, List<Integer> statuses, int page, int size);

    Page<Appointment> findCurrentByVacancyAndUserAndStatusesAndRoles(Long vacancyId, Long userProfileId, List<Integer> statuses, List<Long> roleIds, int page, int size);

    Page<Appointment> findPastByVacancyAndUserAndStatuses(Long vacancyId, Long userProfileId, List<Integer> statuses, int page, int size);

    Page<Appointment> findPastByVacancyAndUserAndStatusesAndRoles(Long vacancyId, Long userProfileId, List<Integer> statuses, List<Long> roleIds, int page, int size);

    Page<VacancyHasAppointment> findHavingAppointmentsOpeningVacancyByUserIdAndCompanyId(Long userProfileId, long companyId, List<String> largerRoles, int page, int size);

    List<Appointment> findAppointmentInSuspendedRangeOfVacancy(Vacancy vacancy);

    int countAppointmentInVacancy(long vacancyId, long managerId);
}
