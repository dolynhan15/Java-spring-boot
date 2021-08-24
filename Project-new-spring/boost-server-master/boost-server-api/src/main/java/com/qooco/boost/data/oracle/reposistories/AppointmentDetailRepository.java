
package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.qooco.boost.data.oracle.reposistories.RepositoryUtils.PAGEABLE_HOLDER;

@Repository
public interface AppointmentDetailRepository extends Boot2JpaRepository<AppointmentDetail, Long> {

    String joinQuery = "FROM APPOINTMENT_DETAIL ad " +
            "JOIN STAFF st " +
            "ON ad.UPDATED_BY = st.USER_PROFILE_ID " +
            "AND ad.UPDATED_BY <> :ownedId " +
            "AND ad.APPOINTMENT_ID IN (:appointmentIds) " +
            "AND ad.STATUS IN (:statuses) " +
            "AND ad.IS_DELETED = 0 " +
            "JOIN ROLE_COMPANY rc " +
            "ON st.ROLE_ID = rc.ROLE_ID " +
            "AND rc.NAME IN (:roleNames) ";

    String STAFF_OF_COMPANY = "SELECT DISTINCT (s.STAFF_ID) FROM STAFF s " +
                              "WHERE s.ROLE_ID IN (1,2,3) " +
                              "AND s.IS_DELETED = 0 " +
                              "AND s.COMPANY_ID = :companyId " +
                              "AND s.USER_PROFILE_ID = :userId ";

    String EXPIRED_EVENTS_EXCEPT_CANDIDATE_FEEDBACK_QUERY = "FROM APPOINTMENT_DETAIL ad " +
            "JOIN APPOINTMENT ap ON (ap.APPOINTMENT_ID = ad.APPOINTMENT_ID " +
            "                        AND ap.IS_DELETED = 0 " +
            "                        AND ad.IS_DELETED = 0 " +
            "                        AND ap.STAFF_ID IN (" + STAFF_OF_COMPANY + ") " +
            "                        ) " +
            "JOIN VACANCY v ON (v.VACANCY_ID = ap.VACANCY_ID " +
            "                   AND IS_SUSPEND_RANGE(v.STATUS, v.START_SUSPEND_DATE, v.SUSPEND_DAYS, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "                   AND v.IS_DELETED = 0) " +
            "WHERE ad.CURRICULUM_VITAE_ID NOT IN (SELECT DISTINCT(vc.CANDIDATE_ID) FROM VACANCY_CANDIDATE vc " +
            "                                     WHERE (vc.VACANCY_ID = ap.VACANCY_ID " +
            "                                     AND vc.CANDIDATE_ID = ad.CURRICULUM_VITAE_ID " +
            "                                     AND vc.IS_DELETED = 0 ) " +
            "                                     ) " +
            "AND ad.STATUS = 12 AND ad.APPOINTMENT_TIME < SYS_EXTRACT_UTC(SYSTIMESTAMP) ";

    String EXPIRED_EVENTS_EXCEPT_CANDIDATE_FEEDBACK = "SELECT DISTINCT(ad.APPOINTMENT_DETAIL_ID) "
            + EXPIRED_EVENTS_EXCEPT_CANDIDATE_FEEDBACK_QUERY ;

    String VACANCY_CANDIDATE_OF_EXPIRED_EVENTS_EXCEPT_CANDIDATE_FEEDBACK = "SELECT ad.CURRICULUM_VITAE_ID, ap.VACANCY_ID "
            + EXPIRED_EVENTS_EXCEPT_CANDIDATE_FEEDBACK_QUERY +
            "AND (:appointmentIdsAreNull = 1 OR ap.APPOINTMENT_ID IN :appointmentIds) " +
            "AND CONCAT(ad.CURRICULUM_VITAE_ID, CONCAT('_', ap.VACANCY_ID)) NOT IN :decideLaterIds ";

    String COUNT_CANDIDATE_OF_EXPIRED_EVENTS_EXCEPT_CANDIDATE_FEEDBACK = "SELECT COUNT(ad.CURRICULUM_VITAE_ID) "
            + EXPIRED_EVENTS_EXCEPT_CANDIDATE_FEEDBACK_QUERY +
            "AND (:appointmentIdsAreNull = 1 OR ap.APPOINTMENT_ID IN :appointmentIds) " +
            "AND CONCAT(ad.CURRICULUM_VITAE_ID, CONCAT('_', ap.VACANCY_ID)) NOT IN :decideLaterIds ";


    @Query(value = "SELECT t.beginningOfDay, count(t.APPOINTMENT_DETAIL_ID) FROM " +
            "(SELECT adr.APPOINTMENT_DATE AS beginningOfDay, ap.APPOINTMENT_DETAIL_ID " +
            " FROM APPOINTMENT_DETAIL ap JOIN APPOINTMENT a ON ap.APPOINTMENT_ID = a.APPOINTMENT_ID " +
            " JOIN APPOINTMENT_DATE_RANGE adr ON adr.APPOINTMENT_ID = a.APPOINTMENT_ID " +
            " WHERE a.STAFF_ID = :staffId " +
            "AND ap.APPOINTMENT_TIME >= :startDate " +
            "AND ap.APPOINTMENT_TIME <= :endDate  " +
            "AND ap.STATUS IN :statusAppointments " +
            "AND ap.APPOINTMENT_TIME >= adr.APPOINTMENT_DATE " +
            "AND ap.APPOINTMENT_TIME < (adr.APPOINTMENT_DATE + INTERVAL '1' DAY)) t " +
            "GROUP BY t.beginningOfDay", nativeQuery = true)
    List<Object[]> countAppointmentByManager(
            @Param("staffId") Long staffId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("statusAppointments") List<Integer> statusAppointments);


    @Query(value = "SELECT t.beginningOfDay, count(t.APPOINTMENT_DETAIL_ID) FROM " +
            "(SELECT adr.APPOINTMENT_DATE AS beginningOfDay, ap.APPOINTMENT_DETAIL_ID " +
            " FROM APPOINTMENT_DETAIL ap JOIN APPOINTMENT a ON ap.APPOINTMENT_ID = a.APPOINTMENT_ID " +
            " JOIN APPOINTMENT_DATE_RANGE adr ON adr.APPOINTMENT_ID = a.APPOINTMENT_ID " +
            " WHERE a.STAFF_ID = :staffId " +
            "AND ap.APPOINTMENT_TIME >= :startDate  " +
            "AND ap.STATUS IN :statusAppointments  " +
            "AND ap.APPOINTMENT_TIME >= adr.APPOINTMENT_DATE " +
            "AND ap.APPOINTMENT_TIME < (adr.APPOINTMENT_DATE + INTERVAL '1' DAY)) t " +
            "GROUP BY t.beginningOfDay", nativeQuery = true)
    List<Object[]> countAppointmentByManagerFromDate(
            @Param("staffId") Long staffId,
            @Param("startDate") Date startDate,
            @Param("statusAppointments") List<Integer> statusAppointments);

    @Query(value = "SELECT t.beginningOfDay, count(t.APPOINTMENT_DETAIL_ID) FROM " +
            "(SELECT adr.APPOINTMENT_DATE AS beginningOfDay, ap.APPOINTMENT_DETAIL_ID " +
            " FROM APPOINTMENT_DETAIL ap JOIN APPOINTMENT a ON ap.APPOINTMENT_ID = a.APPOINTMENT_ID " +
            " JOIN APPOINTMENT_DATE_RANGE adr ON adr.APPOINTMENT_ID = a.APPOINTMENT_ID " +
            " JOIN STAFF s ON s.STAFF_ID = a.STAFF_ID " +
            " JOIN ROLE_COMPANY r ON s.ROLE_ID = r.ROLE_ID " +
            " WHERE (a.STAFF_ID = :staffId OR (r.NAME IN (:roles) AND s.COMPANY_ID = :companyId)) " +
            "AND ap.APPOINTMENT_TIME >= :startDate " +
            "AND ap.APPOINTMENT_TIME <= :endDate  " +
            "AND ap.STATUS IN :statusAppointments  " +
            "AND ap.APPOINTMENT_TIME >= adr.APPOINTMENT_DATE " +
            "AND ap.APPOINTMENT_TIME < (adr.APPOINTMENT_DATE + INTERVAL '1' DAY)) t " +
            "GROUP BY t.beginningOfDay", nativeQuery = true)
    List<Object[]> countAppointmentByUserInCompanyWithRoles(
            @Param("staffId") Long staffId,
            @Param("companyId") Long companyId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("statusAppointments") List<Integer> statusAppointments,
            @Param("roles") List<String> roles);

    @Query(value = "SELECT ad FROM AppointmentDetail  ad WHERE ad.appointment.id = :appointmentId " +
            "AND ad.isDeleted IN :isDeletes")
    List<AppointmentDetail> findByAppointmentId(
            @Param("appointmentId") Long appointmentId,
            @Param("isDeletes") List<Boolean> isDeletes);

    @Query(value = "SELECT t.beginningOfDay, count(t.APPOINTMENT_DETAIL_ID) FROM " +
            "(SELECT adr.APPOINTMENT_DATE AS beginningOfDay, ap.APPOINTMENT_DETAIL_ID " +
            " FROM APPOINTMENT_DETAIL ap JOIN APPOINTMENT a ON ap.APPOINTMENT_ID = a.APPOINTMENT_ID " +
            " JOIN APPOINTMENT_DATE_RANGE adr ON adr.APPOINTMENT_ID = a.APPOINTMENT_ID " +
            " JOIN STAFF s ON s.STAFF_ID = a.STAFF_ID " +
            " JOIN ROLE_COMPANY r ON s.ROLE_ID = r.ROLE_ID " +
            " WHERE (a.STAFF_ID = :staffId OR (r.NAME IN (:roles) AND s.COMPANY_ID = :companyId)) " +
            "AND ap.APPOINTMENT_TIME >= :startDate  " +
            "AND ap.STATUS IN :statusAppointments  " +
            "AND ap.APPOINTMENT_TIME >= adr.APPOINTMENT_DATE " +
            "AND ap.APPOINTMENT_TIME < (adr.APPOINTMENT_DATE + INTERVAL '1' DAY)) t " +
            "GROUP BY t.beginningOfDay", nativeQuery = true)
    List<Object[]> countAppointmentByUserInCompanyFromDateWithRoles(
            @Param("staffId") Long staffId,
            @Param("companyId") Long companyId,
            @Param("startDate") Date startDate,
            @Param("statusAppointments") List<Integer> statusAppointments,
            @Param("roles") List<String> roles);

    @Query(value = "SELECT ad FROM AppointmentDetail ad " +
            "WHERE ad.userCurriculumVitae.userProfile.userProfileId = :userProfileId " +
            "AND ad.status IN :statuses " +
            //TODO: Need check appointment pending and expried or not
            "AND ad.appointmentTime >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "ORDER BY ad.appointmentTime ASC")
    Page<AppointmentDetail> findByUserProfileIdAndStatus(
            @Param("userProfileId") Long userProfileId,
            @Param("statuses") List<Integer> statuses,
            Pageable pageable);

    @Query(value = "SELECT ad FROM AppointmentDetail ad " +
            "WHERE ad.userCurriculumVitae.userProfile.userProfileId = :userProfileId " +
            "AND ad.status IN :statuses " +
            //TODO: Need check appointment pending and expried or not
            "AND ad.appointmentTime >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND ad.appointmentTime >= :startDate " +
            "AND (:endDate IS NULL OR ad.appointmentTime <= :endDate ) " +
            "ORDER BY ad.appointmentTime ASC")
    List<AppointmentDetail> findByUserProfileIdAndStatus(
            @Param("userProfileId") Long userProfileId,
            @Param("statuses") List<Integer> statuses,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query(value = "SELECT * FROM( " +
            "    SELECT ad.* FROM APPOINTMENT_DETAIL ad  " +
            "    JOIN APPOINTMENT ap ON ap.APPOINTMENT_ID = ad.APPOINTMENT_ID " +
            "    JOIN STAFF st ON st.STAFF_ID = ap.STAFF_ID AND st.COMPANY_ID = :companyId " +
            "    JOIN USER_PROFILE pf ON pf.USER_PROFILE_ID = st.USER_PROFILE_ID " +
            "    WHERE pf.USER_PROFILE_ID = :userId " +
            "    AND ad.STATUS IN :statuses " +
            "    AND ad.APPOINTMENT_TIME >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "    AND ad.UPDATED_DATE > :lastTime " +
            "    ORDER BY ad.UPDATED_DATE ASC) " +
            "WHERE (:limit = 0 OR ROWNUM <= :limit) ", nativeQuery = true)
    List<AppointmentDetail> findByUserFitAndStatusAndCompany(
            @Param("companyId") long companyId,
            @Param("userId") Long userId,
            @Param("statuses") List<Integer> statuses,
            @Param("lastTime") Date lastTime,
            @Param("limit") int limit);
    @Query(value = "SELECT * FROM( " +
            "    SELECT ad.* FROM APPOINTMENT_DETAIL ad  " +
            "    JOIN APPOINTMENT ap ON ap.APPOINTMENT_ID = ad.APPOINTMENT_ID " +
            "    JOIN STAFF st ON st.STAFF_ID = ap.STAFF_ID " +
            "    JOIN USER_PROFILE pf ON pf.USER_PROFILE_ID = st.USER_PROFILE_ID " +
            "    WHERE pf.USER_PROFILE_ID = :userId " +
            "    AND ad.STATUS IN :statuses " +
            "    AND ad.APPOINTMENT_TIME >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "    AND ad.UPDATED_DATE > :lastTime " +
            "    ORDER BY ad.UPDATED_DATE ASC) " +
            "WHERE (:limit = 0 OR ROWNUM <= :limit) ", nativeQuery = true)
    List<AppointmentDetail> findByUserFitAndStatus(
            @Param("userId") Long userId,
            @Param("statuses") List<Integer> statuses,
            @Param("lastTime") Date lastTime,
            @Param("limit") int limit);

    @Query(value = "SELECT * FROM( " +
            "    SELECT ad.* FROM APPOINTMENT_DETAIL ad  " +
            "    JOIN APPOINTMENT ap ON ap.APPOINTMENT_ID = ad.APPOINTMENT_ID " +
            "    JOIN USER_CURRICULUM_VITAE cv ON cv.CURRICULUM_VITAE_ID = ad.CURRICULUM_VITAE_ID " +
            "    JOIN USER_PROFILE pf ON pf.USER_PROFILE_ID = cv.USER_PROFILE_ID " +
            "    WHERE pf.USER_PROFILE_ID = :userId " +
            "    AND ad.STATUS IN :statuses " +
            "    AND ad.APPOINTMENT_TIME >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "    AND ad.UPDATED_DATE > :lastTime " +
            "    ORDER BY ad.UPDATED_DATE ASC) " +
            "WHERE (:limit = 0 OR ROWNUM <= :limit)", nativeQuery = true)
    List<AppointmentDetail> findByUserProfileIdAndStatus(
            @Param("userId") Long userId,
            @Param("statuses") List<Integer> statuses,
            @Param("lastTime") Date lastTime,
            @Param("limit") int limit);

    @Query(value = "SELECT COUNT(ad.id) FROM AppointmentDetail ad WHERE ad.userCurriculumVitae.userProfile.userProfileId = :userProfileId " +
            "AND ad.status IN :statuses")
    int countByUserProfileIdAndStatus(
            @Param("userProfileId") Long userProfileId,
            @Param("statuses") List<Integer> statuses);

    @Query("SELECT COUNT(ad.id) FROM AppointmentDetail ad WHERE ad.appointment.id = :appointmentId AND ad.isDeleted = false ")
    int countByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Query(value = "SELECT * FROM APPOINTMENT_DETAIL ad " +
            "JOIN USER_CURRICULUM_VITAE ucv " +
            "ON ad.CURRICULUM_VITAE_ID = ucv.CURRICULUM_VITAE_ID " +
            "AND ad.APPOINTMENT_TIME >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND ad.STATUS IN (:statuses) AND ad.IS_DELETED = 0 " +
            "JOIN USER_PROFILE up " +
            "ON ucv.USER_PROFILE_ID = up.USER_PROFILE_ID " +
            "AND up.USER_PROFILE_ID = :userProfileId " +
            "ORDER BY ad.APPOINTMENT_TIME ASC", nativeQuery = true)
    List<AppointmentDetail> findByCandidateAndStatus(
            @Param("userProfileId") Long userProfileId,
            @Param("statuses") List<Integer> statuses);

    @Query("SELECT ad FROM AppointmentDetail ad " +
            "WHERE ad.appointment.manager.userFit.userProfileId = :userProfileId " +
            "AND ad.appointment.manager.company.companyId = :companyId " +
            "AND ad.status IN (:statuses) " +
            "AND ad.appointmentTime >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND ad.isDeleted = false " +
            "ORDER BY ad.appointmentTime ASC ")
    Page<AppointmentDetail> findAvailableLatestEventByUserProfileIdAndCompanyIdAndStatuses(
            @Param("userProfileId") Long userProfileId,
            @Param("companyId") Long companyId,
            @Param("statuses") List<Integer> statuses,
            Pageable pageable);

    @Query("SELECT COUNT(ad.id) FROM AppointmentDetail ad " +
            "WHERE (ad.appointment.manager.userFit.userProfileId = :userProfileId OR ad.appointment.manager.role.roleId IN :roleIds)" +
            "AND ad.appointment.manager.company.companyId = :companyId " +
            "AND ad.status IN (:statuses) " +
            "AND ad.appointmentTime >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND ad.isDeleted = false " +
            "ORDER BY ad.appointmentTime ASC ")
    int countAvailableByUserProfileIdAndCompanyIdAndStatusesAndRoles(
            @Param("userProfileId") Long userProfileId,
            @Param("companyId") Long companyId,
            @Param("statuses") List<Integer> statuses,
            @Param("roleIds") List<Long> roleIds);

    @Query("SELECT COUNT(ad.id) FROM AppointmentDetail ad " +
            "WHERE ad.appointment.manager.userFit.userProfileId = :userProfileId " +
            "AND ad.appointment.manager.company.companyId = :companyId " +
            "AND ad.status IN (:statuses) " +
            "AND ad.appointmentTime >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND ad.isDeleted = false " +
            "ORDER BY ad.appointmentTime ASC ")
    int countAvailableByUserProfileIdAndCompanyIdAndStatuses(
            @Param("userProfileId") Long userProfileId,
            @Param("companyId") Long companyId,
            @Param("statuses") List<Integer> statuses);

    @Query("SELECT ad FROM AppointmentDetail ad " +
            "WHERE ad.appointment.manager.userFit.userProfileId = :userProfileId " +
            "AND ad.status IN (:statuses) " +
            "AND ad.appointmentTime >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND ad.isDeleted = false " +
            "ORDER BY ad.appointmentTime ASC ")
    Page<AppointmentDetail> findAvailableByUserProfileIdAndStatuses(
            @Param("userProfileId") Long userProfileId,
            @Param("statuses") List<Integer> statuses,
            Pageable pageable);

    @Query("SELECT COUNT(ad.id) FROM AppointmentDetail ad WHERE ad.appointment.id = :appointmentId " +
            "AND ad.status IN (:statuses) " +
            "AND ad.appointmentTime >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND ad.isDeleted = false " +
            "ORDER BY ad.appointmentTime ASC ")
    int countAvailableByAppointmentIdAndStatuses(
            @Param("appointmentId") Long appointmentId,
            @Param("statuses") List<Integer> statuses);

    @Query("SELECT ad FROM AppointmentDetail ad WHERE ad.appointment.id = :appointmentId " +
            "AND ad.status IN (:statuses) " +
            "AND ad.isDeleted = false ")
    List<AppointmentDetail> findByAppointmentIdAndStatuses(
            @Param("appointmentId") Long appointmentId,
            @Param("statuses") List<Integer> statuses);

    @Query("SELECT ad FROM AppointmentDetail ad WHERE ad.appointment.id IN :appointmentIds " +
            "AND ad.status IN (:statuses) " +
            "AND ad.isDeleted = false ")
    List<AppointmentDetail> findByAppointmentIdsAndStatuses(
            @Param("appointmentIds") List<Long> appointmentIds,
            @Param("statuses") List<Integer> statuses);

    @Query(value = "SELECT ad FROM AppointmentDetail ad WHERE ad.id IN :ids AND ad.isDeleted = false")
    List<AppointmentDetail> findByIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT ad.appointment FROM AppointmentDetail ad WHERE ad.id IN :ids AND ad.isDeleted = false")
    List<Appointment> findAppointmentByIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT ad FROM AppointmentDetail ad WHERE ad.id = :id AND ad.isDeleted = false")
    AppointmentDetail findValidById(@Param("id") Long id);

    @Query("SELECT a from AppointmentDetail a WHERE a.appointment.manager.staffId= :staffId " +
            "AND a.appointment.vacancy.company.companyId = :companyId " +
            "AND a.appointmentTime >= :startDate " +
            "AND a.appointmentTime < :endDate " +
            "AND a.status IN (:statuses) " +
            "AND a.isDeleted = false " +
            "ORDER BY a.appointmentTime ASC")
    List<AppointmentDetail> getAppointmentDetailByCompanyIdInDuration(
            @Param("companyId") Long companyId,
            @Param("staffId") Long staffId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("statuses") List<Integer> statuses);

    @Query("SELECT a from AppointmentDetail a WHERE (a.appointment.manager.staffId= :staffId OR a.appointment.manager.role.name IN (:roles)) " +
            "AND a.appointment.vacancy.company.companyId = :companyId " +
            "AND a.appointmentTime >= :startDate " +
            "AND a.appointmentTime < :endDate " +
            "AND a.status IN (:statuses) " +
            "AND a.isDeleted = false " +
            "ORDER BY a.appointmentTime ASC")
    List<AppointmentDetail> getAppointmentDetailByCompanyIdInDurationWithRoles(
            @Param("companyId") Long companyId,
            @Param("staffId") Long staffId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("statuses") List<Integer> statuses,
            @Param("roles") List<String> roles);


    //Find all available user profile's slot (candidate) and all manager's slot who make an appointment with candidate
    @Query("SELECT a from AppointmentDetail a WHERE (a.appointment.manager.staffId = :staffId OR a.userCurriculumVitae.userProfile.userProfileId = :userProfileId) " +
            "AND a.appointmentTime >= :startDate " +
            "AND a.appointmentTime < :endDate " +
            "AND a.status IN (:statuses) " +
            "AND a.isDeleted = false " +
            "ORDER BY a.appointmentTime ASC")
    List<AppointmentDetail> getAllAppointmentDetailByStaffOrUserProfileInDuration(
            @Param("staffId") Long staffId,
            @Param("userProfileId") Long userProfileId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("statuses") List<Integer> statuses);


    @Query("SELECT a from AppointmentDetail a WHERE a.appointmentId = :appointmentId " +
            "AND a.appointmentTime >= :startDate " +
            "AND a.appointmentTime < :endDate " +
            "AND a.status IN (:statuses) " +
            "AND a.isDeleted = false ")
    List<AppointmentDetail> findAcceptedAppointmentDetailInDuration(
            @Param("appointmentId") Long appointmentId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("statuses") List<Integer> acceptedStatuses);

    @Query("SELECT COUNT(a.id) from AppointmentDetail a WHERE a.appointment.manager.staffId= :staffId " +
            "AND a.appointment.vacancy.company.companyId = :companyId " +
            "AND a.appointmentTime >= :eventTime " +
            "AND a.status IN (:statuses) " +
            "AND a.isDeleted = false ")
    int checkExistsAppointmentDetailByCompanyIdInDuration(
            @Param("companyId") Long companyId,
            @Param("staffId") Long staffId,
            @Param("eventTime") Date eventTime,
            @Param("statuses") List<Integer> statuses);

    @Query(value = "SELECT ad FROM AppointmentDetail ad WHERE ad.appointment.id IN :appointmentIds " +
            "AND ad.isDeleted = false " +
            "ORDER BY ad.appointment.id ASC ")
    List<AppointmentDetail> findByAppointmentIds(@Param("appointmentIds") List<Long> appointmentIds);

    @Query(value = "SELECT ad.APPOINTMENT_ID, ad.APPOINTMENT_DETAIL_ID " + joinQuery, nativeQuery = true)
    List<Object[]> findIdByUpdaterWithStatusAndRoles(
            @Param("ownedId") Long ownedId,
            @Param("appointmentIds") List<Long> appointmentIds,
            @Param("statuses") List<Integer> statuses,
            @Param("roleNames") List<String> roleNames);

    @Query(value = "SELECT ad FROM AppointmentDetail ad WHERE ad.isDeleted = false  " +
            "AND ad.appointment.id = :appointmentId  " +
            "AND ad.status IN :statuses " +
            "AND ad.userCurriculumVitae.curriculumVitaeId IN :userCvId " +
            "ORDER BY ad.appointment.id ASC ")
    List<AppointmentDetail> findByAppointmentIdAndUserCurriculumVitaeIdAndStatus(
            @Param("appointmentId") Long appointmentId,
            @Param("userCvId") List<Long> userCvIds,
            @Param("statuses") List<Integer> statuses);

    @Query("SELECT COUNT(ad.id) FROM AppointmentDetail ad WHERE ad.appointment.id = :appointmentId " +
            "AND ad.userCurriculumVitae.userProfile.userProfileId = :userProfileId " +
            "AND ad.isDeleted = false ")
    int checkExistsByIdAndUserProfileId(
            @Param("appointmentId") Long appointmentId,
            @Param("userProfileId") Long userProfileId);

    @Query("SELECT ad FROM AppointmentDetail ad WHERE ad.appointment.vacancy.id IN :vacancyIds " +
            "AND ad.appointment.manager.staffId = :staffId " +
            "AND ad.status IN :statuses " +
            "AND ad.appointmentTime >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND ad.appointment.appointmentDate >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND IS_SUSPEND_RANGE(ad.appointment.vacancy.status, ad.appointment.vacancy.startSuspendDate, ad.appointment.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "AND ad.isDeleted = false " +
            "ORDER BY ad.appointmentTime ASC")
    List<AppointmentDetail> findAvailableByVacancyIdsAndStatusesNoRoles(
            @Param("vacancyIds") List<Long> vacancyIds,
            @Param("statuses") List<Integer> statuses,
            @Param("staffId") Long staffId);

    @Query("SELECT ad FROM AppointmentDetail ad WHERE ad.appointment.vacancy.id IN :vacancyIds " +
            "AND (ad.appointment.manager.staffId = :staffId  OR ad.appointment.manager.role.name IN (:roles)) " +
            "AND ad.status IN :statuses " +
            "AND ad.appointmentTime >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND ad.appointment.appointmentDate >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND IS_SUSPEND_RANGE(ad.appointment.vacancy.status, ad.appointment.vacancy.startSuspendDate, ad.appointment.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "AND ad.isDeleted = false " +
            "ORDER BY ad.appointmentTime ASC")
    List<AppointmentDetail> findAvailableByVacancyIdsAndStatusesWithRoles(
            @Param("vacancyIds") List<Long> vacancyIds,
            @Param("statuses") List<Integer> statuses,
            @Param("roles") List<String> roles,
            @Param("staffId") Long staffId);

    @Query("SELECT ap FROM AppointmentDetail ap WHERE ap.appointment.vacancy.id IN :vacancyIds " +
            "AND ap.status IN :statuses " +
            "AND (ap.appointment.manager.userFit.userProfileId = :userProfileId  OR ap.appointment.manager.role.name IN (:roles)) " +
            "AND ap.appointment.vacancy.company.companyId = :companyId " +
            "AND IS_SUSPEND_RANGE(ap.appointment.vacancy.status, ap.appointment.vacancy.startSuspendDate, ap.appointment.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "AND ap.appointment.appointmentDate >= SYS_EXTRACT_UTC(SYSTIMESTAMP) ")
    List<AppointmentDetail> findHavingActiveAppointmentDetailByUserIdAndCompanyIdWithRoles(
            @Param("userProfileId") Long userProfileId,
            @Param("companyId") long companyId,
            @Param("statuses") List<Integer> statuses,
            @Param("roles") List<String> roles,
            @Param("vacancyIds") List<Long> vacancyIds);

    @Query("SELECT ap FROM AppointmentDetail ap WHERE ap.appointment.vacancy.id IN :vacancyIds " +
            "AND ap.status IN :statuses " +
            "AND ap.appointment.manager.userFit.userProfileId = :userProfileId " +
            "AND ap.appointment.vacancy.company.companyId = :companyId " +
            "AND IS_SUSPEND_RANGE(ap.appointment.vacancy.status, ap.appointment.vacancy.startSuspendDate, ap.appointment.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 " +
            "AND ap.appointment.appointmentDate >= SYS_EXTRACT_UTC(SYSTIMESTAMP) ")
    List<AppointmentDetail> findHavingActiveAppointmentDetailByUserIdAndCompanyIdNoRole(
            @Param("userProfileId") Long userProfileId,
            @Param("companyId") long companyId,
            @Param("statuses") List<Integer> statuses,
            @Param("vacancyIds") List<Long> vacancyIds);

    @Query("SELECT ad FROM AppointmentDetail ad WHERE ad.userCurriculumVitae.curriculumVitaeId = :userCVId " +
            "AND ad.status IN :statuses AND ad.appointment.vacancy.id = :vacancyId " +
            "AND (ad.appointmentTime IS NULL OR ad.appointmentTime >= SYS_EXTRACT_UTC(SYSTIMESTAMP)) AND ad.isDeleted = false ")
    List<AppointmentDetail> findByUserCVIdAndStatusOfVacancyAndNotExpired(
            @Param("userCVId") long userCVId,
            @Param("statuses") List<Integer> statuses,
            @Param("vacancyId") long vacancyId);

    @Query("SELECT ad FROM AppointmentDetail ad WHERE ad.userCurriculumVitae.curriculumVitaeId = :userCVId " +
            "AND ad.status IN :statuses AND ad.appointment.vacancy.id = :vacancyId " +
            "AND (ad.appointmentTime IS NOT NULL AND ad.appointmentTime < SYS_EXTRACT_UTC(SYSTIMESTAMP)) AND ad.isDeleted = false ")
    List<AppointmentDetail> findByUserCVIdAndStatusOfVacancyAndExpired(
            @Param("userCVId") long userCVId,
            @Param("statuses") List<Integer> statuses,
            @Param("vacancyId") long vacancyId);

    @Query("SELECT ad FROM AppointmentDetail ad WHERE ad.status IN :statuses AND ad.appointment.vacancy.id = :vacancyId " +
            "AND (ad.appointmentTime IS NULL OR ad.appointmentTime >= SYS_EXTRACT_UTC(SYSTIMESTAMP)) AND ad.isDeleted = false ")
    List<AppointmentDetail> findByStatusOfVacancyAndNotExpired(
            @Param("statuses") List<Integer> statuses,
            @Param("vacancyId") Long vacancyId);

    @Query("SELECT ap FROM AppointmentDetail ap WHERE ap.appointment.vacancy.id = :vacancyId " +
            "AND IS_SUSPEND_RANGE(ap.appointment.vacancy.status, ap.appointment.vacancy.startSuspendDate, ap.appointment.vacancy.suspendDays, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 2 " +
            "AND ((ap.status = :pendingStatus " +
            "AND ap.appointment.appointmentDate >= :startDate " +
            "AND ap.appointment.appointmentDate <= :endDate ) " +
            " OR (ap.status IN :acceptStatus " +
            "AND ap.appointmentTime IS NOT NULL " +
            "AND ap.appointmentTime >= :startDate " +
            "AND ap.appointmentTime <= :endDate )) ")
    List<AppointmentDetail> findPendingAndAcceptedAppointmentInSuspendRange(@Param("vacancyId")long vacancyId,
                                                                            @Param("startDate")Date startDate,
                                                                            @Param("endDate")Date endDate,
                                                                            @Param("pendingStatus") int pendingStatus,
                                                                            @Param("acceptStatus")List<Integer> acceptStatus);

    @Query(value = EXPIRED_EVENTS_EXCEPT_CANDIDATE_FEEDBACK , nativeQuery = true)
    List<BigDecimal> findExpiredEventByUserAndCompany(@Param("userId") Long userId, @Param("companyId") Long companyId);

    @Query(value = VACANCY_CANDIDATE_OF_EXPIRED_EVENTS_EXCEPT_CANDIDATE_FEEDBACK + PAGEABLE_HOLDER,
            countQuery = COUNT_CANDIDATE_OF_EXPIRED_EVENTS_EXCEPT_CANDIDATE_FEEDBACK,
            nativeQuery = true)
    Page<Object[]> getVacancyCandidateOfExpiredEventsForFeedback(@Param("userId") Long userId,
                                                                 @Param("companyId") Long companyId,
                                                                 @Param("decideLaterIds") List<String> decideLaterIds,
                                                                 @Param("appointmentIdsAreNull") int appointmentIdsAreNull,
                                                                 @Param("appointmentIds") List<Long> appointmentIds,
                                                                 Pageable pageable);

    @Query("SELECT ad FROM AppointmentDetail ad WHERE ad.userCurriculumVitae.curriculumVitaeId = :userCVId " +
            "AND ad.status IN :statuses AND ad.appointment.vacancy.id = :vacancyId " +
            "AND (ad.appointmentTime IS NOT NULL AND ad.appointmentTime < SYS_EXTRACT_UTC(SYSTIMESTAMP) AND ad.appointmentTime > :lastDate) AND ad.isDeleted = false")
    List<AppointmentDetail> findByUserCVIdAndStatusOfVacancyAndExpiredAfter(
            @Param("userCVId") long userCVId,
            @Param("statuses") List<Integer> statuses,
            @Param("vacancyId") long vacancyId,
            @Param("lastDate") Date lastDate);


    @Query("SELECT ad FROM AppointmentDetail ad WHERE ad.appointment.vacancy.id = :vacancyId")
    Page<AppointmentDetail> findByVacancy(@Param("vacancyId") long vacancyId, Pageable pageable);
}