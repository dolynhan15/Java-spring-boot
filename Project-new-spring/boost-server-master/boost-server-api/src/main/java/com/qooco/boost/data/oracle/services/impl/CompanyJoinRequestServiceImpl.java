package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.CompanyJoinRequest;
import com.qooco.boost.data.oracle.reposistories.CompanyJoinRequestRepository;
import com.qooco.boost.data.oracle.services.CompanyJoinRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class CompanyJoinRequestServiceImpl implements CompanyJoinRequestService {

    @Autowired
    private CompanyJoinRequestRepository companyJoinRequestRepository;

    @Override
    public List<CompanyJoinRequest> findByCompanyIdAndUserProfileId(long companyId, long userProfileId) {
        return companyJoinRequestRepository.findByCompanyIdAndUserProfileId(companyId, userProfileId);
    }

    @Override
    public List<CompanyJoinRequest> findPendingJoinRequestByCompanyAndUserProfile(long companyId, long userProfileId) {
        return companyJoinRequestRepository.findPendingJoinRequestByCompanyAndUserProfile(companyId, userProfileId);
    }


    @Override
    public CompanyJoinRequest save(CompanyJoinRequest joinRequest) {
        return companyJoinRequestRepository.save(joinRequest);
    }

    @Override
    public CompanyJoinRequest findById(Long id) {
        return companyJoinRequestRepository.findById(id).orElse(null);
    }

    @Override
    public Page<CompanyJoinRequest> findLastRequest(Long userProfileId, int page, int size) {
        return companyJoinRequestRepository.findLastPendingJoinRequestsByUser(userProfileId, PageRequest.of(page, size));
    }

    @Override
    public List<Object[]> findCompanyIdWithPendingAndApprovedJoinRequestByUserId(Long userId) {
        return companyJoinRequestRepository.findCompanyIdWithPendingAndApprovedJoinRequestByUserId(userId);
    }


    @Override
    public List<CompanyJoinRequest> findByCompanyId(long companyId) {
        return companyJoinRequestRepository.findAllByCompanyCompanyId(companyId);
    }
}
