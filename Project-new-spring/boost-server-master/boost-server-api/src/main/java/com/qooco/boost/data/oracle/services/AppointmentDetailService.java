package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.model.VacancyHasAppointment;
import com.qooco.boost.data.model.count.CountByDate;
import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface AppointmentDetailService {
    AppointmentDetail save(AppointmentDetail appointmentDetail);

    List<AppointmentDetail> save(List<AppointmentDetail> appointmentDetails);

    AppointmentDetail findById(Long id);

    List<AppointmentDetail> findByIds(List<Long> ids);

    List<Appointment> findAppointmentByIds(List<Long> ids);

    List<AppointmentDetail> findByAppointmentId(Long appointmentId);

    AppointmentDetail findOneByVacancy(Long vacancyId);

    List<AppointmentDetail> findByAppointmentIds(List<Long> appointmentIds);

    List<AppointmentDetail> findByAppointmentId(Long appointmentId, Boolean isDelete);

    List<AppointmentDetail> findAcceptedAppointmentInDuration(Long appointmentId, Date fromDate, Date toDate);

    Page<AppointmentDetail> findByUserProfileIdAndStatus(Long userProfileId, List<Integer> statuses, int page, int size);

    List<AppointmentDetail> findByUserProfileIdAndStatus(Long userProfileId, List<Integer> statuses, Date fromDate, Date toDate);

    List<AppointmentDetail> findByUserFitAndStatus(Long userId, Long companyId, List<Integer> statuses, Date lastTime, int limit);

    List<AppointmentDetail> findByUserProfileAndStatus(Long userId, List<Integer> statuses, Date lastTime, int limit);

    List<CountByDate> countAppointmentByManager(Long staffId, Long startDate, Long endDate, String timeZoneOffset);

    List<CountByDate> countAppointmentByUserInCompany(Long staffId, Long companyId, Long startDate, Long endDate, String timeZoneOffset, List<String> largerRoles);

    List<AppointmentDetail> getAppointmentDetailByCompanyIdInDuration(Long companyId, Long staffId, Date startDate, Date endDate, List<Integer> statuses);

    List<AppointmentDetail> getAppointmentDetailByCompanyIdInDurationWithRoles(Long companyId, Long staffId, Date startDate, Date endDate, List<Integer> statuses, List<String> largerRoles);

    List<AppointmentDetail> getAllAppointmentDetailByManagerAppointmentOrUserProfileInDuration(Long staffId, Long userProfileId, Date startDate, Date endDate, List<Integer> statuses);

    boolean checkExistsAppointmentDetailByCompanyIdInDuration(Long companyId, Long staffId, Date eventTime, List<Integer> statuses);

    List<Object[]> findIdByUpdaterWithStatusAndRoles(Long ownedIds, List<Long> appointmentIds, List<Integer> statuses, List<String> roleNames);

    int countByAppointmentId(Long appointmentId);

    List<AppointmentDetail> findByAppointmentIdAndStatuses(Long appointmentId, List<Integer> availableStatus);

    List<AppointmentDetail> findByAppointmentIdAndStatuses(List<Long> appointmentIds, List<Integer> availableStatus);

    List<Long> findExpiredEventByUserAndCompany(Long userId, Long companyId);

    List<AppointmentDetail> findByAppointmentIdAndUserCVIdAndStatus(Long appointmentId, List<Long> userCVIds, List<Integer> statuses);

    List<AppointmentDetail> findByCandidateAndStatus(Long userProfileId, List<Integer> statuses);

    AppointmentDetail findAvailableByUserProfileIdAndStatuses(Long userProfileId, List<Integer> statuses);

    AppointmentDetail findAvailableLatestEventByUserProfileIdAndCompanyIdAndStatuses(Long userProfileId, Long companyId, List<Integer> statuses);

    int countAvailableByUserProfileIdAndCompanyIdAndStatusesAndRoles(Long userProfileId, Long companyId, List<Integer> statuses, List<Long> roles);

    int countAvailableByUserProfileIdAndCompanyIdAndStatuses(Long userProfileId, Long companyId, List<Integer> statuses);

    int countAvailableByAppointmentIdAndStatuses(Long appointmentId, List<Integer> statuses);

    boolean checkExistsByIdAndUserProfileId(Long appointmentId, Long userProfileId);

    List<AppointmentDetail> findAvailableByVacancyIdsAndStatuses(List<Long> vacancyIds, List<Integer> statuses, List<String> roles, long staffId);

    Page<VacancyHasAppointment> findOpeningVacancyHavingAppointmentsByUserAndCompany(Long userProfileId, long companyId, List<String> largerRoles, int page, int size);

    List<AppointmentDetail> findPendingAndAcceptedAppointmentInSuspendRange(long vacancyId, Date startDate, Date endDate);

    List<AppointmentDetail> findByUserCVIdAndStatusOfVacancyAndExpired(long userCVId, List<Integer> statuses, long vacancyId);

    List<AppointmentDetail> findByUserCVIdAndStatusOfVacancyAndNotExpired(long userCVId, List<Integer> statuses, long vacancyId);

    List<AppointmentDetail> findByStatusOfVacancyAndNotExpired(List<Integer> statuses, Long vacancyId);

    Page<Object[]> getVacancyCandidateExpiredEventsOfAppointmentForFeedBack(Long userId, Long companyId, List<String> decideLaterIds, boolean appointmentIdIsNull, Long appointmentId, int size);

    List<AppointmentDetail> findAcceptedByUserCVIdOfVacancyAndExpiredAfter(long userCVId, long vacancyId, Date date);
}
