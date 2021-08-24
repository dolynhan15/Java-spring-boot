package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.CompanyJoinRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CompanyJoinRequestRepository extends Boot2JpaRepository<CompanyJoinRequest, Long> {
    @Query("SELECT c FROM CompanyJoinRequest c WHERE c.userFit.userProfileId = :userProfileId " +
            "AND c.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED " +
            "AND c.isDeleted = false " +
            "AND c.company.isDeleted = false " +
            "AND c.company.companyId = :companyId")
    List<CompanyJoinRequest> findByCompanyIdAndUserProfileId(@Param("companyId") long companyId, @Param("userProfileId") long userProfileId);

    @Query("SELECT c FROM CompanyJoinRequest c WHERE c.userFit.userProfileId = :userProfileId " +
            "AND c.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED " +
            "AND c.isDeleted = false " +
            "AND c.status = com.qooco.boost.data.enumeration.JoinCompanyStatus.PENDING " +
            "AND c.company.isDeleted = false " +
            "AND c.company.companyId = :companyId")
    List<CompanyJoinRequest> findPendingJoinRequestByCompanyAndUserProfile(@Param("companyId") long companyId, @Param("userProfileId") long userProfileId);

    List<CompanyJoinRequest> findAllByCompanyCompanyId(Long companyId);

    @Query("SELECT c FROM CompanyJoinRequest c WHERE c.userFit.userProfileId = :userProfileId " +
            "AND c.company.status = com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED " +
            "AND c.isDeleted = false AND c.company.isDeleted = false " +
            "AND c.status = com.qooco.boost.data.enumeration.JoinCompanyStatus.PENDING " +
            "ORDER BY c.createdDate DESC , c.updatedDate DESC ")
    Page<CompanyJoinRequest> findLastPendingJoinRequestsByUser(@Param("userProfileId") long userProfileId, Pageable pageable);

    @Query(value = "SELECT DISTINCT(jq.COMPANY_ID), jq.STATUS FROM COMPANY_JOIN_REQUEST jq " +
            "WHERE jq.USER_PROFILE_ID = :userId AND jq.STATUS IN (0,1) AND jq.IS_DELETED = 0 ", nativeQuery = true)
    List<Object[]> findCompanyIdWithPendingAndApprovedJoinRequestByUserId(@Param("userId") Long userId);
}
