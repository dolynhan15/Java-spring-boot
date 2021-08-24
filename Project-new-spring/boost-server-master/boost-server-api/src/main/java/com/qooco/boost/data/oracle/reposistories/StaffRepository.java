package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.oracle.entities.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StaffRepository extends Boot2JpaRepository<Staff, Long> {

    @Query("SELECT s FROM Staff s " +
            "WHERE s.staffId = :staffId " +
            "AND s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false ")
    Staff findByIdAnCompanyApproved(@Param("staffId") Long staffId);

    @Query("SELECT s FROM Staff s " +
            "WHERE s.staffId IN :staffIds " +
            "AND s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false ")
    List<Staff> findById(@Param("staffIds") List<Long> staffIds);


    @Query("SELECT count(s) FROM Staff s WHERE s.userFit.userProfileId = :adminId AND s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.PENDING AND s.isDeleted = false AND s.company.isDeleted = false AND s.role.roleId = 1")
    int countPendingCompanyOfAdmin(@Param("adminId") long adminId);

    @Query("SELECT s FROM Staff s " +
            "WHERE s.userFit.userProfileId = :userProfileId " +
            "AND s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.PENDING " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.company.companyId = :companyId " +
            "AND s.role.roleId = 1 " +
            "AND ROWNUM = 1 ")
    Staff findByPendingCompanyAndAdmin(@Param("companyId") long companyId, @Param("userProfileId") long userProfileId);

    @Query("SELECT s FROM Staff s " +
            "WHERE s.userFit.userProfileId = :userProfileId " +
            "AND s.company.status = 1 " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.company.companyId = :companyId " +
            "AND s.role.roleId = 1")
    List<Staff> findByCompanyApprovalAndAdmin(@Param("companyId") long companyId, @Param("userProfileId") long userProfileId);

    @Query("SELECT s FROM Staff s " +
            "WHERE s.company.status = 1 " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.role.roleId = :roleId")
    List<Staff> findByRole(@Param("roleId") Long roleId);

    @Query("SELECT s FROM Staff s WHERE s.userFit.userProfileId = :userProfileId AND s.isDeleted = false AND s.company.isDeleted = false AND s.company.companyId = :companyId AND s.role.roleId = 1")
    List<Staff> findByCompanyAndAdmin(@Param("companyId") long companyId, @Param("userProfileId") long userProfileId);

    @Query("SELECT count(s) FROM Staff s " +
            "WHERE s.userFit.userProfileId = :userProfileId " +
            "AND s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.userFit.isDeleted = false " +
            "AND s.company.companyId = :companyId " +
            "AND s.role.name IN (:roles)")
    int countByCompanyAndUserProfileAndRoles(@Param("companyId") long companyId, @Param("userProfileId") long userProfileId, @Param("roles") List<String> roles);

    @Query("SELECT s FROM Staff s WHERE s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED AND s.isDeleted = false AND s.company.isDeleted = false AND s.userFit.isDeleted = false AND s.company.companyId = :companyId AND s.role.name IN (:roles) AND s.userFit.isDeleted = false AND s.userFit.userProfileId <> :userProfileId")
    Page<Staff> findStaffListByRoles(@Param("companyId") long companyId, @Param("userProfileId") long userProfileId, @Param("roles") List<String> roles, Pageable pageable);

    @Query("SELECT s FROM Staff s " +
            "WHERE s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.company.companyId = :companyId " +
            "AND s.staffId IN :staffIds " +
            "AND s.role.name IN (:roles) ")
    List<Staff> findStaffByIdsAndCompanyIdAndRoles(@Param("companyId") Long companyId, @Param("staffIds") List<Long> staffIds, @Param("roles") List<String> roles);

    @Query("SELECT s FROM Staff s " +
            "WHERE s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.userFit.isDeleted = false " +
            "AND s.company.companyId = :companyId " +
            "AND s.role.name IN (:roles) " +
            "ORDER BY GET_DISPLAY_NAME(s.userFit.firstName, s.userFit.lastName, s.userFit.username) ASC  ")
    Page<Staff> findStaffListByRoles(@Param("companyId") long companyId, @Param("roles") List<String> roles, Pageable pageable);

    @Query("SELECT s FROM Staff s WHERE s.userFit.userProfileId IN :userProfileIds " +
            "AND s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.company.companyId = :companyId")
    List<Staff> findByCompanyAndUserProfileIds(@Param("companyId") long companyId, @Param("userProfileIds") List<Long> userProfileIds);

    @Query("SELECT s FROM Staff s WHERE s.role.roleId = :roleId AND s.isDeleted = false AND s.company.isDeleted = false AND s.company.companyId = :companyId")
    List<Staff> findStaffOfCompanyByRole(@Param("companyId") long companyId, @Param("roleId") long roleId);

    @Query("SELECT s FROM Staff s " +
            "WHERE s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.userFit.isDeleted = false " +
            "AND (:ignoreUserIds IS NULL OR s.userFit.userProfileId NOT IN (:ignoreUserIds)) " +
            "AND s.company.companyId = :companyId " +
            "AND s.role IS NOT NULL " +
            "ORDER BY GET_DISPLAY_NAME(s.userFit.firstName, s.userFit.lastName, s.userFit.username) ASC ")
    Page<Staff> findByCompany(@Param("companyId") long companyId, @Param("ignoreUserIds") List<Long> ignoreUserIds, Pageable pageable);

    @Query("SELECT s FROM Staff s " +
            "WHERE s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.company.companyId = :companyId " +
            "AND s.role IS NOT NULL " +
            "AND s.userFit.isDeleted = false " +
            "AND s.userFit.userProfileId <> :userProfileId")
    Page<Staff> findByCompany(@Param("companyId") long companyId, @Param("userProfileId") long userProfileId, Pageable pageable);

    @Query("SELECT s FROM Staff s WHERE s.userFit.userProfileId = :userProfileId " +
            "AND s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.company.companyId = :companyId " +
            "AND s.role IS NOT NULL")
    List<Staff> findByUserProfileAndCompanyApproved(@Param("userProfileId") long userProfileId, @Param("companyId") long companyId);

    @Query("SELECT s FROM Staff s WHERE s.userFit.userProfileId = :userProfileId " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.company.companyId = :companyId " +
            "AND s.role IS NOT NULL")
    List<Staff> findByUserProfileAndCompany(@Param("userProfileId") long userProfileId, @Param("companyId") long companyId);

    @Query("SELECT COUNT(s.staffId) FROM Staff s WHERE s.userFit.userProfileId = :userProfileId " +
            "AND s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.company.companyId = :companyId " +
            "AND s.role IS NOT NULL")
    int countByUserProfileAndCompany(@Param("userProfileId") long userProfileId, @Param("companyId") long companyId);

    @Query("SELECT s FROM Staff s " +
            "WHERE s.userFit.userProfileId = :userProfileId " +
            "AND s.company.status = :companyStatus " +
            "AND s.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "ORDER BY s.createdDate ASC, s.updatedBy ASC")
    List<Staff> findByCompanyStatusAndUserProfile(@Param("userProfileId") long userProfileId, @Param("companyStatus") ApprovalStatus companyStatus);

    @Query("SELECT s FROM Staff s WHERE s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED AND s.isDeleted = false AND s.company.isDeleted = false AND s.company.companyId = :companyId AND s.staffId = :staffId AND s.userFit.isDeleted = false")
    Staff findByStaffIdAndCompany(@Param("staffId") long staffId, @Param("companyId") long companyId);

    @Query("SELECT s FROM Staff s " +
            "WHERE s.userFit.userProfileId = :userProfileId " +
            "AND s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED " +
            "AND s.company.companyId = :companyId " +
            "AND s.userFit.isDeleted = false " +
            "AND s.company.isDeleted = false " +
            "AND s.isDeleted = false " +
            "AND s.role.name IN (:roles) ")
    List<Staff> findByUserProfileAndCompanyApprovalIdAndRoles(@Param("userProfileId") Long userProfileId, @Param("companyId") Long companyId, @Param("roles") List<String> roles);

    @Query("SELECT s.userFit.userProfileId FROM Staff s WHERE s.company.companyId = :companyId AND s.company.isDeleted = false " +
            "AND s.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED AND s.role.name IN :roles AND s.isDeleted = false ")
    List<Long> findUserProfileIdByCompanyIdAndRoles(@Param("companyId") Long companyId, @Param("roles") List<String> roles);

    @Query("SELECT s FROM Staff s WHERE s.isDeleted = false AND s.company.isDeleted = false AND s.company.companyId IN :companyIds AND s.userFit.isDeleted = false")
    List<Staff> findByCompanyIds(@Param("companyIds") List<Long> companyIds);

    @Query("SELECT COUNT(s.staffId) FROM Staff s WHERE s.company.companyId = :companyId AND s.isDeleted = false " +
            "AND s.company.isDeleted = false AND s.userFit.isDeleted = false AND s.role IS NOT NULL ")
    int countByCompany(@Param("companyId") Long companyId);

    @Query(value = "SELECT " +
            "st.STAFF_ID, u.USERNAME, u.FIT_FIRST_NAME, u.FIT_LAST_NAME, u.FIT_AVATAR, u.FIT_PHONE_NUMBER, u.FIT_NATIONAL_ID, c.COUNTRY_ID, c.COUNTRY_NAME, c.COUNTRY_CODE, c.PHONE_CODE, " +
            "COUNT(DISTINCT CASE WHEN vs.CLOSED_DATE IS NOT NULL AND vs.CLOSED_DATE <= :endDate AND vs.CLOSED_DATE >= :startDate THEN vs.ID END) closedCandidates, " +
            "count(DISTINCT CASE WHEN " +
            "   ((vs1.STATUS = 1 AND vs1.CREATED_DATE < :endDate) " +
            "   OR (vs1.STATUS = 3 AND vs1.SUSPEND_FROM_DATE IS NOT NULL AND vs1.SUSPEND_FROM_DATE > :endDate AND vs1.CREATED_DATE < :beginningOfEndDate) " +
            "   OR (vs1.STATUS = 4 AND vs1.SUSPEND_FROM_DATE IS NOT NULL AND vs1.SUSPEND_FROM_DATE > :endDate AND vs1.CREATED_DATE < :beginningOfEndDate) " +
            "   OR (vs1.STATUS = 5 AND vs1.DELETED_DATE IS NOT NULL AND vs1.DELETED_DATE > :endDate AND vs1.CREATED_DATE < :beginningOfEndDate) " +
            "   OR (vs1.STATUS = 2 AND vs1.CLOSED_DATE IS NOT NULL AND vs1.CLOSED_DATE > :endDate AND vs1.CREATED_DATE < :beginningOfEndDate))  THEN vs1.ID end) openSeats, " +
            "count(DISTINCT CASE WHEN vp.ID IS NOT NULL AND vp.CREATED_DATE <= :endDate AND vp.CREATED_DATE >= :startDate THEN vp.ID END) candidateProcessing, " +
            "count(DISTINCT CASE WHEN af.ID IS NOT NULL AND af.FEEDBACK_DATE <= :endDate AND af.FEEDBACK_DATE >= :startDate THEN af.ID END) appointments, " +
            "ROUND(AVG(CASE WHEN sw.activeMins IS NOT NULL THEN  sw.activeMins ELSE 0 END)) active_Mins " +
            "FROM STAFF st " +
            "JOIN USER_PROFILE u ON st.USER_PROFILE_ID = u.USER_PROFILE_ID "+
            "LEFT JOIN COUNTRY c ON u.COUNTRY_ID = c.COUNTRY_ID "+
            "LEFT JOIN VACANCY_SEAT vs ON st.STAFF_ID = vs.CLOSED_STAFF_ID " +
            "LEFT JOIN VACANCY_SEAT vs1 ON st.STAFF_ID = vs1.RESPONSIBLE_STAFF_ID " +
            "LEFT JOIN VACANCY_PROCESSING vp ON st.STAFF_ID = vp.STAFF_ID " +
            "LEFT JOIN APPOINTMENT_FEEDBACK af ON st.STAFF_ID = af.STAFF_ID " +
            "LEFT JOIN (SELECT temp.STAFF_ID, sum( " +
            "     CASE WHEN temp.END_DATE > SYS_EXTRACT_UTC(SYSTIMESTAMP) THEN " +
            "    ((cast(SYS_EXTRACT_UTC(SYSTIMESTAMP) as date) - cast(temp.START_DATE as date)) * 24 * 60) " +
            "    ELSE ((cast(temp.END_DATE as date) - cast(temp.START_DATE as date)) * 24 * 60) END " +
            "  ) activeMins FROM STAFF_WORKING temp WHERE temp.START_DATE >= :startDate AND temp.END_DATE <= :endDate GROUP  BY temp.STAFF_ID) sw ON st.STAFF_ID = sw.STAFF_ID " +
            "WHERE st.COMPANY_ID = :companyId "+
            "GROUP BY (st.STAFF_ID, u.USERNAME, u.FIT_FIRST_NAME, u.FIT_LAST_NAME, u.FIT_AVATAR, u.FIT_PHONE_NUMBER, u.FIT_NATIONAL_ID, c.COUNTRY_ID, c.COUNTRY_NAME, c.COUNTRY_CODE, c.PHONE_CODE) "+
            "order by closedCandidates desc, st.STAFF_ID desc ",
            countQuery = "SELECT COUNT(st.STAFF_ID) FROM STAFF st WHERE st.COMPANY_ID = :companyId ",
            nativeQuery = true)
    Page<Object[]> findStaffStatisticByCompany(@Param("companyId") Long companyId,
                                               @Param("startDate")Date startDate,
                                               @Param("beginningOfEndDate")Date beginningOfEndDate,
                                               @Param("endDate")Date endDate,
                                               Pageable pageable);

    @Query(value = "SELECT " +
            "st.STAFF_ID, u.USERNAME, u.FIT_FIRST_NAME, u.FIT_LAST_NAME, u.FIT_AVATAR, u.FIT_PHONE_NUMBER, u.FIT_NATIONAL_ID, c.COUNTRY_ID, c.COUNTRY_NAME, c.COUNTRY_CODE, c.PHONE_CODE, " +
            "COUNT(DISTINCT CASE WHEN vs.CLOSED_DATE IS NOT NULL AND vs.CLOSED_DATE <= :endDate AND vs.CLOSED_DATE >= :startDate THEN vs.ID END) closedCandidates " +
            "FROM STAFF st " +
            "LEFT JOIN USER_PROFILE u ON st.USER_PROFILE_ID = u.USER_PROFILE_ID "+
            "LEFT JOIN COUNTRY c ON u.COUNTRY_ID = c.COUNTRY_ID "+
            "LEFT JOIN VACANCY_SEAT vs ON st.STAFF_ID = vs.CLOSED_STAFF_ID AND vs.CLOSED_DATE <= :endDate AND vs.CLOSED_DATE >= :startDate " +
            "WHERE st.COMPANY_ID = :companyId " +
            "GROUP BY (st.STAFF_ID, u.USERNAME, u.FIT_FIRST_NAME, u.FIT_LAST_NAME, u.FIT_AVATAR, u.FIT_PHONE_NUMBER, u.FIT_NATIONAL_ID, c.COUNTRY_ID, c.COUNTRY_NAME, c.COUNTRY_CODE, c.PHONE_CODE) " +
            "order by closedCandidates desc, st.STAFF_ID DESC ",
            countQuery = "SELECT COUNT(st.STAFF_ID) FROM STAFF st WHERE st.COMPANY_ID = :companyId ",
            nativeQuery = true)
    Page<Object[]> findStaffClosedSeatByCompany(@Param("companyId") Long companyId,
                                                @Param("startDate")Date startDate,
                                                @Param("endDate")Date endDate,
                                                Pageable pageable);

    @Query(value = "SELECT  " +
            "st.STAFF_ID,  " +
            "count(DISTINCT CASE WHEN  " +
            "   ((vs1.STATUS = 1 AND vs1.CREATED_DATE < :endDate)  " +
            "   OR (vs1.STATUS = 3 AND vs1.SUSPEND_FROM_DATE IS NOT NULL AND vs1.SUSPEND_FROM_DATE > :endDate AND vs1.CREATED_DATE < :beginningOfEndDate)  " +
            "   OR (vs1.STATUS = 4 AND vs1.SUSPEND_FROM_DATE IS NOT NULL AND vs1.SUSPEND_FROM_DATE > :endDate AND vs1.CREATED_DATE < :beginningOfEndDate)  " +
            "   OR (vs1.STATUS = 5 AND vs1.DELETED_DATE IS NOT NULL AND vs1.DELETED_DATE > :endDate AND vs1.CREATED_DATE < :beginningOfEndDate)  " +
            "   OR (vs1.STATUS = 2 AND vs1.CLOSED_DATE IS NOT NULL AND vs1.CLOSED_DATE > :endDate AND vs1.CREATED_DATE < :beginningOfEndDate))  THEN vs1.ID end) openSeats " +
            "FROM STAFF st " +
            "LEFT JOIN VACANCY_SEAT vs1 ON st.STAFF_ID = vs1.RESPONSIBLE_STAFF_ID   " +
            "WHERE vs1.CREATED_DATE < :endDate AND st.STAFF_ID IN :staffIds  " +
            "GROUP BY st.STAFF_ID ",
            nativeQuery = true)
    List<Object[]> findStaffOpenSeatByStaffs(@Param("endDate")Date endDate,
                                             @Param("beginningOfEndDate")Date beginningOfEndDate,
                                             @Param("staffIds")List<Long> staffIds);

    @Query(value = "SELECT " +
            "st.STAFF_ID, " +
            "count(DISTINCT CASE WHEN vp.ID IS NOT NULL AND vp.CREATED_DATE <= :endDate AND vp.CREATED_DATE >= :startDate THEN vp.ID END) candidateProcessing " +
            "FROM STAFF st " +
            "LEFT JOIN VACANCY_PROCESSING vp ON st.STAFF_ID = vp.STAFF_ID " +
            "WHERE st.STAFF_ID IN :staffIds AND vp.CREATED_DATE <= :endDate AND vp.CREATED_DATE >= :startDate " +
            "GROUP BY st.STAFF_ID ",
            nativeQuery = true)
    List<Object[]> findStaffCandidateProcessingByStaffs(@Param("startDate")Date startDate,
                                              @Param("endDate")Date endDate,
                                                        @Param("staffIds")List<Long> staffIds);

    @Query(value = "SELECT  " +
            "st.STAFF_ID,  " +
            "count(DISTINCT CASE WHEN af.ID IS NOT NULL AND af.FEEDBACK_DATE <= :endDate AND af.FEEDBACK_DATE >= :startDate THEN af.ID END) appointments " +
            "FROM STAFF st " +
            "LEFT JOIN APPOINTMENT_FEEDBACK af ON st.STAFF_ID = af.STAFF_ID  " +
            "WHERE st.STAFF_ID IN :staffIds AND af.FEEDBACK_DATE <= :endDate AND af.FEEDBACK_DATE >= :startDate " +
            "GROUP BY st.STAFF_ID ",
            nativeQuery = true)
    List<Object[]> findStaffAppointmentFeedbackByStaffs(@Param("startDate")Date startDate,
                                                         @Param("endDate")Date endDate,
                                                         @Param("staffIds")List<Long> staffIds);

    @Query(value = "SELECT  " +
            "st.STAFF_ID,  " +
            "ROUND(AVG(CASE WHEN sw.activeMins IS NOT NULL THEN  sw.activeMins ELSE 0 END)) active_Mins " +
            "FROM STAFF st " +
            "LEFT JOIN (SELECT temp.STAFF_ID, sum(  " +
            "     CASE WHEN temp.END_DATE > SYS_EXTRACT_UTC(SYSTIMESTAMP) THEN  " +
            "    ((cast(SYS_EXTRACT_UTC(SYSTIMESTAMP) as date) - cast(temp.START_DATE as date)) *  1440)  " +
            "    ELSE ((cast(temp.END_DATE as date) - cast(temp.START_DATE as date)) *  1440) END  " +
            "  ) activeMins FROM STAFF_WORKING temp WHERE temp.START_DATE >= :startDate AND temp.END_DATE <= :endDate AND temp.STAFF_ID IN :staffIds GROUP  BY temp.STAFF_ID) sw ON st.STAFF_ID = sw.STAFF_ID  " +
            "WHERE st.STAFF_ID IN :staffIds " +
            "GROUP BY st.STAFF_ID ",
            nativeQuery = true)
    List<Object[]> findStaffActiveMinsByStaffs(@Param("startDate")Date startDate,
                                                        @Param("endDate")Date endDate,
                                                        @Param("staffIds")List<Long> staffIds);
}
