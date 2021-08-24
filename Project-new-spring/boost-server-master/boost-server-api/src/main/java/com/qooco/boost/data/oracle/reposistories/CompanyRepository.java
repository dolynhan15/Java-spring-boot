package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.oracle.entities.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CompanyRepository extends Boot2JpaRepository<Company, Long> {

    @Modifying
    @Query(value = "UPDATE Company c SET c.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED WHERE c.companyId = :companyId")
    int updateCompanyStatusByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT c FROM Company c WHERE c.companyId = :id AND c.isDeleted = false")
    Company findValidById(@Param("id") Long id);

    @Query("SELECT COUNT(c) FROM Company c WHERE c.companyId = :id AND c.isDeleted = false")
    int countById(@Param("id") long id);

    @Query("SELECT c FROM Company c WHERE c.companyId = :id AND c.status = :status AND c.isDeleted = false")
    Company findByIdAndStatus(@Param("id") long id, @Param("status") int status);

    List<Company> findByCompanyIdInAndStatusAndIsDeletedFalse(Long[] ids, ApprovalStatus status);

    @Query("SELECT c FROM Company c WHERE c.status = :status AND c.isDeleted = false")
    List<Company> findByStatus(@Param("status") ApprovalStatus status);

    @Query("SELECT c FROM Company c WHERE c.status = com.qooco.boost.data.enumeration.ApprovalStatus.PENDING AND c.isDeleted = false AND c.createdBy = :userProfileId ORDER BY c.createdDate DESC , c.updatedDate DESC ")
    Page<Company> findLastPendingCompanyCreatedByUser(@Param("userProfileId") Long userProfileId, Pageable pageable);

    Page<Company> findByCity_province_country_countryIdAndStatusOrderByCompanyName(Long countryId, ApprovalStatus status, Pageable pageable);

    @Query(value = "SELECT DISTINCT c.* FROM COMPANY c " +
            "JOIN STAFF s ON s.COMPANY_ID = c.COMPANY_ID " +
            "   AND c.STATUS IN :status " +
            "   AND s.IS_DELETED = 0 " +
            "   AND c.IS_DELETED = 0 " +
            "   AND s.USER_PROFILE_ID = :userProfileId " +
            "   AND s.ROLE_ID IN :roles " +
            "ORDER BY c.COMPANY_NAME ASC ", nativeQuery = true)
    List<Company> findByUserProfileAndRoleAndStatuses(@Param("userProfileId") Long userProfileId,
                                                      @Param("roles") List<Long> roles,
                                                      @Param("status") List<Integer> status);

    @Query(value = "SELECT DISTINCT c.* FROM COMPANY c " +
            "JOIN STAFF s ON s.COMPANY_ID = c.COMPANY_ID " +
            "LEFT JOIN COMPANY_JOIN_REQUEST jq ON jq.COMPANY_ID = c.COMPANY_ID " +
            "WHERE (s.USER_PROFILE_ID = :userId AND s.IS_DELETED = 0) " +
            "OR (jq.USER_PROFILE_ID = :userId AND jq.STATUS = 0 AND c.STATUS = 1 AND jq.IS_DELETED = 0) " +
            "AND c.IS_DELETED = 0 " +
            "ORDER BY c.COMPANY_NAME ASC ", nativeQuery = true)
    List<Company> findCompanyOfStaffOrJoinRequestByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT COUNT(DISTINCT COMPANY_ID) FROM (SELECT DISTINCT c.COMPANY_ID FROM COMPANY c JOIN STAFF s ON c.COMPANY_ID = s.COMPANY_ID WHERE s.USER_PROFILE_ID = :userId AND s.IS_DELETED = 0 AND c.IS_DELETED = 0 AND c.STATUS = 0 " +
            " UNION SELECT DISTINCT r.COMPANY_ID FROM COMPANY_JOIN_REQUEST r WHERE r.USER_PROFILE_ID = :userId AND r.STATUS = 0 AND r.IS_DELETED = 0)", nativeQuery = true)
    int countPendingJoinedCompanyRequestAndCompanyAuthorizationRequestByUser(@Param("userId") Long userId);
}
