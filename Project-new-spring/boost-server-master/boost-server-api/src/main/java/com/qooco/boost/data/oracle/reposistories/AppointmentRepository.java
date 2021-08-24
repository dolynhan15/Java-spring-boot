package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.model.VacancyHasAppointment;
import com.qooco.boost.data.oracle.entities.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.qooco.boost.data.oracle.reposistories.RepositoryUtils.ORDERBY_PAGEABLE_HOLDER;

@Repository
public interface AppointmentRepository extends Boot2JpaRepository<Appointment, Long> {

    String APPOINTMENT_AVAILABLE = "SELECT ap.APPOINTMENT_ID FROM APPOINTMENT ap " +
            "JOIN STAFF s ON s.STAFF_ID = ap.STAFF_ID " +
            "WHERE ap.IS_DELETED = 0 " +
            "AND ap.APPOINTMENT_DATE >= SYS_EXTRACT_UTC(SYSTIMESTAMP) ";

    final String APPOINTMENT_AVAILABLE_HAS_APPOINTMENT_DETAIL_STATUS = "SELECT ad.APPOINTMENT_ID FROM APPOINTMENT_DETAIL ad " +
            "WHERE ad.APPOINTMENT_ID IN (" + APPOINTMENT_AVAILABLE + ")" +
            "AND ad.IS_DELETED = 0 " +
            "AND ad.STATUS IN :appointmentDetailStatus " +
            "AND ap.APPOINTMENT_DATE >= SYS_EXTRACT_UTC(SYSTIMESTAMP) ";

    @Modifying
    @Transactional
    @Query(value = "UPDATE Appointment ap SET ap.isDeleted = :isDelete, ap.updatedBy = :updatedBy, ap.updatedDate = :updatedDate " +
            "WHERE ap.id = :id")
    int updateDeleteStatus(@Param("id") Long id, @Param("isDelete") boolean isDelete, @Param("updatedBy") Long updatedBy, @Param("updatedDate") Date updatedDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Appointment ap SET ap.isDeleted = :isDelete, ap.updatedBy = :updatedBy, ap.updatedDate = :updatedDate " +
            "WHERE ap.id IN :ids")
    int updateDeleteStatus(@Param("ids") List<Long> ids, @Param("isDelete") boolean isDelete, @Param("updatedBy") Long updatedBy, @Param("updatedDate") Date updatedDate);

    @Query(value = "SELECT ap FROM Appointment ap WHERE ap.vacancy.id = :vacancyId " +
            "AND ap.vacancy.isDeleted = false " +
            "AND ap.isDeleted = false " +
            "AND ap.appointmentDate >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND IS_SUSPEND_RANGE(ap.vacancy.status, ap.vacancy.startSuspendDate, ap.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "ORDER BY ap.appointmentDate ASC ")
    List<Appointment> findNotExpiredByVacancyId(@Param("vacancyId") Long vacancyId);

    @Query(value = "SELECT ap FROM Appointment ap WHERE ap.vacancy.id = :vacancyId AND ap.location.locationId = :locationId AND ap.manager.staffId = :managerId AND ap.appointmentDate = :appointmentDate")
    List<Appointment> findByVacancyIdAndManagerIdAndLocationIdAndAppointmentDate(@Param("vacancyId") Long vacancyId, @Param("managerId") Long managerId, @Param("locationId") Long locationId, @Param("appointmentDate") Date appointmentDate);

    @Query(value = "SELECT COUNT (ap.id) FROM Appointment ap WHERE ap.id IN :ids AND ap.isDeleted = false ")
    int countByIds(@Param("ids") List<Long> ids);

    @Query("SELECT ap FROM Appointment ap WHERE ap.vacancy.company.companyId = :companyId " +
            "AND ap.vacancy.isDeleted = false AND ap.vacancy.company.isDeleted = false " +
            "AND (ap.updatedBy = :userProfileId OR ap.manager.userFit.userProfileId = :userProfileId " +
            "OR ap.vacancy.updatedBy = :userProfileId) AND ap.isDeleted = false " +
            "AND ap.appointmentDate >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND IS_SUSPEND_RANGE(ap.vacancy.status, ap.vacancy.startSuspendDate, ap.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "ORDER BY ap.appointmentDate ASC ")
    List<Appointment> findNotExpiredByCompanyIdAndUserProfileId(@Param("companyId") Long companyId, @Param("userProfileId") Long userProfileId);

    @Query("SELECT ap FROM Appointment ap WHERE ap.vacancy.company.companyId = :companyId " +
            "AND ap.vacancy.isDeleted = false AND ap.vacancy.company.isDeleted = false " +
            "AND ap.isDeleted = false " +
            "AND ap.appointmentDate >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND IS_SUSPEND_RANGE(ap.vacancy.status, ap.vacancy.startSuspendDate, ap.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "ORDER BY ap.appointmentDate ASC ")
    List<Appointment> findNotExpiredByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT ap FROM Appointment ap " +
            "WHERE ap.vacancy.id = :vacancyId " +
            "AND ap.vacancy.isDeleted = false " +
            "AND ap.isDeleted = false " +
            "AND (ap.updatedBy = :userProfileId " +
            "OR ap.manager.userFit.userProfileId = :userProfileId " +
            "OR ap.vacancy.updatedBy = :userProfileId) " +
            "AND ap.appointmentDate >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND IS_SUSPEND_RANGE(ap.vacancy.status, ap.vacancy.startSuspendDate, ap.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "ORDER BY ap.appointmentDate ASC ")
    List<Appointment> findNotExpiredByVacancyIdAndUserProfileId(@Param("vacancyId") Long vacancyId, @Param("userProfileId") Long userProfileId);

    @Query("SELECT ap FROM Appointment ap WHERE ap.vacancy.company.companyId = :companyId " +
            "AND ap.vacancy.isDeleted = false " +
            "AND ap.vacancy.company.isDeleted = false " +
            "AND (ap.updatedBy = :userProfileId OR ap.manager.userFit.userProfileId = :userProfileId OR ap.vacancy.updatedBy = :userProfileId) " +
            "AND ap.isDeleted = false " +
            "AND IS_SUSPEND_RANGE(ap.vacancy.status, ap.vacancy.startSuspendDate, ap.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "ORDER BY ap.appointmentDate ASC ")
    List<Appointment> findByCompanyIdAndUserProfileId(@Param("companyId") Long companyId, @Param("userProfileId") Long userProfileId);

    @Query("SELECT ap FROM Appointment ap WHERE ap.vacancy.company.companyId = :companyId " +
            "AND ap.vacancy.isDeleted = false " +
            "AND ap.vacancy.company.isDeleted = false " +
            "AND ap.isDeleted = false " +
            "AND IS_SUSPEND_RANGE(ap.vacancy.status, ap.vacancy.startSuspendDate, ap.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "ORDER BY ap.appointmentDate ASC ")
    List<Appointment> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT ap.id FROM Appointment ap " +
            "WHERE ap.manager.staffId = :staffId " +
            "AND ap.isDeleted = false " +
            "AND ap.manager.isDeleted = false " +
            "AND ap.vacancy.isDeleted = false " +
            "AND ap.vacancy.company.isDeleted = false " +
            "ORDER BY ap.appointmentDate ASC ")
    List<Long> findByStaff(@Param("staffId") Long staffId);

    @Query(value = "SELECT ap.* FROM APPOINTMENT ap " +
            "WHERE ap.APPOINTMENT_ID IN (" + APPOINTMENT_AVAILABLE_HAS_APPOINTMENT_DETAIL_STATUS + ") " +
            "AND ap.APPOINTMENT_ID IN  :ids ", nativeQuery = true)
    List<Appointment> findAvailableByIdsAndAppointmentDetailStatus(@Param("ids") List<Long> ids, @Param("appointmentDetailStatus") List<Integer> appointmentDetailStatus);

    @Query("SELECT ap FROM Appointment ap WHERE ap.id = :id AND ap.isDeleted = false ")
    Appointment findValidById(@Param("id") Long id);

    @Query(value = "SELECT ap FROM Appointment ap WHERE ap.vacancy.id = :vacancyId AND ap.vacancy.isDeleted = false " +
            "AND ap.isDeleted = false ORDER BY ap.appointmentDate ASC ")
    List<Appointment> findByVacancyId(@Param("vacancyId") Long vacancyId);

    @Query(value = "SELECT ap FROM Appointment ap " +
            "WHERE ap.vacancy.id = :vacancyId " +
            "AND ap.vacancy.isDeleted = false " +
            "AND IS_SUSPEND_RANGE(ap.vacancy.status, ap.vacancy.startSuspendDate, ap.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "AND ap.isDeleted = false " +
            "ORDER BY ap.appointmentDate ASC ")
    List<Appointment> findByOpeningVacancyId(@Param("vacancyId") Long vacancyId);

    @Query("SELECT ap FROM Appointment ap WHERE ap.vacancy.id = :vacancyId " +
            "AND ap.vacancy.isDeleted = false " +
            "AND (ap.updatedBy = :userProfileId OR ap.manager.userFit.userProfileId = :userProfileId OR ap.vacancy.updatedBy = :userProfileId) " +
            "AND IS_SUSPEND_RANGE(ap.vacancy.status, ap.vacancy.startSuspendDate, ap.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "AND ap.isDeleted = false " +
            "ORDER BY ap.appointmentDate ASC ")
    List<Appointment> findByVacancyIdAndUserProfileId(@Param("vacancyId") Long vacancyId, @Param("userProfileId") Long userProfileId);

    @Query("SELECT ap FROM Appointment ap WHERE ap.manager.userFit.userProfileId = :userProfileId " +
            "AND ap.appointmentDate >= SYS_EXTRACT_UTC(SYSTIMESTAMP) AND ap.isDeleted = false ORDER BY ap.appointmentDate ASC ")
    List<Appointment> findNearestAppointmentByUserProfileId(@Param("userProfileId") Long userProfileId);


    @Query(value = "SELECT * FROM APPOINTMENT apt WHERE apt.APPOINTMENT_ID IN " +
            "(SELECT ap.APPOINTMENT_ID FROM APPOINTMENT ap " +
            "JOIN APPOINTMENT_DETAIL ad ON ad.APPOINTMENT_ID = ap.APPOINTMENT_ID " +
            "JOIN STAFF s ON s.STAFF_ID = ap.STAFF_ID " +
            "WHERE ad.APPOINTMENT_TIME >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND ad.STATUS IN :statuses AND ad.IS_DELETED = 0 " +
            "AND ap.VACANCY_ID = :vacancyId " +
            "AND s.USER_PROFILE_ID = :userProfileId " +
            "AND ap.IS_DELETED = 0 " +
            "AND ap.APPOINTMENT_DATE >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "UNION " +
            "SELECT ap.APPOINTMENT_ID FROM APPOINTMENT ap " +
            "JOIN STAFF s ON s.STAFF_ID = ap.STAFF_ID " +
            "WHERE ap.VACANCY_ID = :vacancyId " +
            "AND s.USER_PROFILE_ID = :userProfileId " +
            "AND ap.IS_DELETED = 0 " +
            "AND ap.APPOINTMENT_DATE >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            ") " +
            ORDERBY_PAGEABLE_HOLDER, nativeQuery = true)
    Page<Appointment> findCurrentByVacancyIdAndUserProfileIdAndStatuses(@Param("vacancyId") Long vacancyId,
                                                                        @Param("userProfileId") Long userProfileId,
                                                                        @Param("statuses") List<Integer> statuses,
                                                                        Pageable pageable);

    @Query(value = "SELECT * FROM APPOINTMENT apt WHERE apt.APPOINTMENT_ID IN " +
            "(SELECT ap.APPOINTMENT_ID FROM APPOINTMENT ap " +
            "JOIN APPOINTMENT_DETAIL ad ON ad.APPOINTMENT_ID = ap.APPOINTMENT_ID " +
            "JOIN STAFF s ON s.STAFF_ID = ap.STAFF_ID " +
            "WHERE ad.APPOINTMENT_TIME >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND ad.STATUS IN :statuses AND ad.IS_DELETED = 0 " +
            "AND ap.VACANCY_ID = :vacancyId " +
            "AND (s.USER_PROFILE_ID = :userProfileId " +
            "OR s.ROLE_ID IN :roleIds) AND ap.IS_DELETED = 0 " +
            "AND ap.APPOINTMENT_DATE >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "UNION " +
            "SELECT ap.APPOINTMENT_ID FROM APPOINTMENT ap " +
            "JOIN STAFF s ON s.STAFF_ID = ap.STAFF_ID " +
            "WHERE ap.VACANCY_ID = :vacancyId " +
            "AND (s.USER_PROFILE_ID = :userProfileId " +
            "OR s.ROLE_ID IN :roleIds) AND ap.IS_DELETED = 0 " +
            "AND ap.APPOINTMENT_DATE >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            ") " +
            ORDERBY_PAGEABLE_HOLDER, nativeQuery = true)
    Page<Appointment> findCurrentByVacancyIdAndUserProfileIdAndStatusesAndRoles(@Param("vacancyId") Long vacancyId,
                                                                                @Param("userProfileId") Long userProfileId,
                                                                                @Param("statuses") List<Integer> statuses,
                                                                                @Param("roleIds") List<Long> roleIds,
                                                                                Pageable pageable);

    @Query(value = "SELECT * FROM APPOINTMENT apt WHERE apt.APPOINTMENT_ID IN ( " +
            "SELECT ap.APPOINTMENT_ID FROM APPOINTMENT ap " +
            "JOIN APPOINTMENT_DETAIL ad ON (ad.APPOINTMENT_ID = ap.APPOINTMENT_ID " +
            "                               AND ad.APPOINTMENT_DETAIL_ID NOT IN ( " +
            "                                   SELECT adsub.APPOINTMENT_DETAIL_ID FROM APPOINTMENT_DETAIL adsub " +
            "                                   JOIN APPOINTMENT apsub ON apsub.APPOINTMENT_ID = adsub.APPOINTMENT_ID " +
            "                                   JOIN VACANCY_CANDIDATE vc ON (vc.VACANCY_ID = apsub.VACANCY_ID AND vc.CANDIDATE_ID = adsub.CURRICULUM_VITAE_ID AND vc.IS_DELETED = 0)) " +
            "                               AND ad.APPOINTMENT_TIME < SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "                               AND ad.STATUS IN :statuses AND ad.IS_DELETED = 0) " +
            "JOIN STAFF s ON (s.STAFF_ID = ap.STAFF_ID " +
            "                 AND (s.USER_PROFILE_ID = :userProfileId OR s.ROLE_ID IN :roleIds)) " +
            "WHERE ap.VACANCY_ID = :vacancyId " +
            "AND ap.IS_DELETED = 0 " +
            "AND ap.FROM_DATE <= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "UNION " +
            "SELECT ap.APPOINTMENT_ID FROM APPOINTMENT ap " +
            "JOIN STAFF s ON (s.STAFF_ID = ap.STAFF_ID AND (s.USER_PROFILE_ID = :userProfileId OR s.ROLE_ID IN :roleIds)) " +
            "WHERE ap.APPOINTMENT_ID NOT IN (SELECT DISTINCT(apsub.APPOINTMENT_ID) FROM APPOINTMENT apsub " +
            "                                JOIN APPOINTMENT_DETAIL adsub ON (adsub.APPOINTMENT_ID = apsub.APPOINTMENT_ID " +
            "                                                                  AND adsub.APPOINTMENT_DETAIL_ID IS NOT NULL " +
            "                                                                  AND adsub.STATUS IN :statuses AND adsub.IS_DELETED = 0) " +
            "                                WHERE apsub.VACANCY_ID = :vacancyId " +
            "                                AND apsub.IS_DELETED = 0)" +
            "AND ap.VACANCY_ID = :vacancyId " +
            "AND ap.IS_DELETED = 0 " +
            "AND ap.APPOINTMENT_DATE <= SYS_EXTRACT_UTC(SYSTIMESTAMP)) " +
            ORDERBY_PAGEABLE_HOLDER, nativeQuery = true)
    Page<Appointment> findPastByVacancyIdAndUserProfileIdAndStatusesAndRoles(@Param("vacancyId") Long vacancyId,
                                                                             @Param("userProfileId") Long userProfileId,
                                                                             @Param("statuses") List<Integer> statuses,
                                                                             @Param("roleIds") List<Long> roleIds,
                                                                             Pageable pageable);

    @Query(value = "SELECT * FROM APPOINTMENT apt WHERE apt.APPOINTMENT_ID IN ( " +
            "SELECT ap.APPOINTMENT_ID FROM APPOINTMENT ap " +
            "JOIN APPOINTMENT_DETAIL ad ON (ad.APPOINTMENT_ID = ap.APPOINTMENT_ID " +
            "                               AND ad.APPOINTMENT_DETAIL_ID NOT IN ( " +
            "                                   SELECT adsub.APPOINTMENT_DETAIL_ID FROM APPOINTMENT_DETAIL adsub " +
            "                                   JOIN APPOINTMENT apsub ON apsub.APPOINTMENT_ID = adsub.APPOINTMENT_ID " +
            "                                   JOIN VACANCY_CANDIDATE vc ON (vc.VACANCY_ID = apsub.VACANCY_ID AND vc.CANDIDATE_ID = adsub.CURRICULUM_VITAE_ID AND vc.IS_DELETED = 0)) " +
            "                               AND ad.APPOINTMENT_TIME < SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "                               AND ad.STATUS IN :statuses AND ad.IS_DELETED = 0) " +
            "JOIN STAFF s ON (s.STAFF_ID = ap.STAFF_ID " +
            "                 AND (s.USER_PROFILE_ID = :userProfileId)) " +
            "WHERE ap.VACANCY_ID = :vacancyId " +
            "AND ap.IS_DELETED = 0 " +
            "AND ap.FROM_DATE <= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "UNION " +
            "SELECT ap.APPOINTMENT_ID FROM APPOINTMENT ap " +
            "JOIN STAFF s ON (s.STAFF_ID = ap.STAFF_ID AND (s.USER_PROFILE_ID = :userProfileId)) " +
            "WHERE ap.APPOINTMENT_ID NOT IN (SELECT DISTINCT(apsub.APPOINTMENT_ID) FROM APPOINTMENT apsub " +
            "                                JOIN APPOINTMENT_DETAIL adsub ON (adsub.APPOINTMENT_ID = apsub.APPOINTMENT_ID " +
            "                                                                  AND adsub.APPOINTMENT_DETAIL_ID IS NOT NULL " +
            "                                                                  AND adsub.STATUS IN :statuses AND adsub.IS_DELETED = 0) " +
            "                                WHERE apsub.VACANCY_ID = :vacancyId " +
            "                                AND apsub.IS_DELETED = 0)" +
            "AND ap.VACANCY_ID = :vacancyId " +
            "AND ap.IS_DELETED = 0 " +
            "AND ap.APPOINTMENT_DATE <= SYS_EXTRACT_UTC(SYSTIMESTAMP)) " +
            ORDERBY_PAGEABLE_HOLDER, nativeQuery = true)
    Page<Appointment> findPastByVacancyIdAndUserProfileIdAndStatuses(@Param("vacancyId") Long vacancyId,
                                                                     @Param("userProfileId") Long userProfileId,
                                                                     @Param("statuses") List<Integer> statuses,
                                                                     Pageable pageable);

    @Query("SELECT new com.qooco.boost.data.model.VacancyHasAppointment(ap.vacancy.id, ap.vacancy, count(ap.id)) " +
            "FROM Appointment ap WHERE ap.isDeleted = false " +
            "AND (ap.manager.userFit.userProfileId = :userProfileId  OR ap.manager.role.name IN (:roles)) " +
            "AND ap.vacancy.company.companyId = :companyId " +
            "AND IS_SUSPEND_RANGE(ap.vacancy.status, ap.vacancy.startSuspendDate, ap.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "GROUP BY ap.vacancy.id ")
    Page<VacancyHasAppointment> findHavingAppointmentsByUserIdAndCompanyIdWithRoles(@Param("userProfileId") Long userProfileId, @Param("companyId") long companyId, @Param("roles") List<String> roles, Pageable pageable);

    @Query("SELECT new com.qooco.boost.data.model.VacancyHasAppointment(ap.vacancy.id, ap.vacancy, count(ap.id)) " +
            "FROM Appointment ap WHERE ap.isDeleted = false " +
            "AND ap.manager.userFit.userProfileId = :userProfileId " +
            "AND ap.vacancy.company.companyId = :companyId " +
            "AND IS_SUSPEND_RANGE(ap.vacancy.status, ap.vacancy.startSuspendDate, ap.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "GROUP BY ap.vacancy.id ")
    Page<VacancyHasAppointment> findHavingAppointmentsByUserIdAndCompanyIdNoRoles(@Param("userProfileId") Long userProfileId, @Param("companyId") long companyId, Pageable pageable);

    @Query("SELECT ap FROM Appointment ap WHERE ap.isDeleted = false AND ap.vacancy.id = :vacancyId AND ap.appointmentDate <= :endSuspendDate AND ap.fromDate >= :startSuspendDate")
    List<Appointment> findAppointmentInSuspendedRangeOfVacancy(@Param("vacancyId") long vacancyId, @Param("startSuspendDate") Date startSuspendDate, @Param("endSuspendDate") Date endSuspendDate);

    @Query("SELECT COUNT(ap.id) FROM Appointment ap WHERE ap.isDeleted = false AND ap.vacancy.id = :vacancyId AND ap.manager.userFit.userProfileId = :managerId")
    int countAppointmentInVacancy(@Param("vacancyId") long vacancyId, @Param("managerId") long managerId);
}
