package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.oracle.entities.Company;
import org.springframework.data.domain.Page;

import java.util.List;


public interface CompanyService {
    Company findById(long id);

    boolean exists(long id);

    Company findByIdAndStatus(long id, int status);

    List<Company> findByIdsAndStatus(Long[] ids, ApprovalStatus status);

    List<Company> findByStatus(ApprovalStatus status);

    Company save(Company company);

    Integer updateCompanyStatus(Long companyId);

    Boolean exists(Long companyId);

    Page<Company> findLastPendingCompanyCreatedByUser(Long userProfileId, int page, int size);
    Page<Company> findByCountryAndStatusApproved(Long countryId, int page, int size);

    Company getDefaultCompanyOfUser(Long userProfileId);

    List<Company> findByUserProfileAndRoleAndStatus(Long userId, List<Long> roles, List<Integer> status);

    List<Company> findCompanyOfStaffOrJoinRequestByUserId(Long userId);

    int countPendingJoinedCompanyRequestAndCompanyAuthorizationRequestByUser(long userId);
}