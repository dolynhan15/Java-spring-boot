package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.CompanyJoinRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CompanyJoinRequestService {
    List<CompanyJoinRequest> findByCompanyIdAndUserProfileId(long companyId, long userProfileId);

    List<CompanyJoinRequest> findByCompanyId(long companyId);

    List<CompanyJoinRequest> findPendingJoinRequestByCompanyAndUserProfile(long companyId, long userProfileId);

    CompanyJoinRequest save(CompanyJoinRequest joinRequest);

    CompanyJoinRequest findById(Long id);

    Page<CompanyJoinRequest> findLastRequest(Long userProfileId, int page, int size);

    List<Object[]> findCompanyIdWithPendingAndApprovedJoinRequestByUserId(Long userId);
}
