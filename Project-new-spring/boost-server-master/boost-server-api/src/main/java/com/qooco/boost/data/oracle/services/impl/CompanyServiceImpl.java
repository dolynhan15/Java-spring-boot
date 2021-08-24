package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.reposistories.CompanyRepository;
import com.qooco.boost.data.oracle.services.CompanyService;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.data.oracle.services.UserFitService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.qooco.boost.data.enumeration.ApprovalStatus.APPROVED;

@Transactional
@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository repository;

    @Autowired
    private StaffService staffService;

    @Autowired
    private UserFitService userFitService;

    @Override
    public Company findById(long id) {
        return repository.findValidById(id);
    }

    @Override
    public boolean exists(long id) {
        int count = repository.countById(id);
        return count > 0;
    }

    @Override
    public List<Company> findByStatus(ApprovalStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public Company findByIdAndStatus(long id, int status) {
        return repository.findByIdAndStatus(id, status);
    }

    @Override
    public List<Company> findByIdsAndStatus(Long[] ids, ApprovalStatus status) {
        return repository.findByCompanyIdInAndStatusAndIsDeletedFalse(ids, status);
    }

    @Override
    public Company save(Company company) {
        if (company != null) {
            return repository.save(company);
        }
        return null;
    }

    @Override
    public Integer updateCompanyStatus(Long companyId) {
        return repository.updateCompanyStatusByCompanyId(companyId);
    }

    @Override
    public Boolean exists(Long companyId) {
        return repository.existsById(companyId);
    }

    @Override
    public Page<Company> findLastPendingCompanyCreatedByUser(Long userProfileId, int page, int size) {
        return repository.findLastPendingCompanyCreatedByUser(userProfileId, PageRequest.of(page, size));
    }

    @Override
    public Page<Company> findByCountryAndStatusApproved(Long countryId, int page, int size) {
        return repository.findByCity_province_country_countryIdAndStatusOrderByCompanyName(countryId, APPROVED, PageRequest.of(page, size));
    }

    @Override
    public Company getDefaultCompanyOfUser(Long userProfileId) {
        UserFit userFit = userFitService.findById(userProfileId);
        Company company = null;
        if (Objects.nonNull(userFit)) {
            // Get the default company
            List<Staff> staff = staffService.findByUserProfileAndStatus(userProfileId, APPROVED);
            if (CollectionUtils.isNotEmpty(staff)) {
                if (Objects.isNull(userFit.getDefaultCompany())) {
                    company = staff.get(0).getCompany();
                } else {
                    company = userFit.getDefaultCompany();
                }
            }
        }
        return company;
    }

    @Override
    public List<Company> findByUserProfileAndRoleAndStatus(Long userProfileId, List<Long> roles, List<Integer> status) {
        return repository.findByUserProfileAndRoleAndStatuses(userProfileId, roles, status);
    }

    @Override
    public List<Company> findCompanyOfStaffOrJoinRequestByUserId(Long userId) {
        return repository.findCompanyOfStaffOrJoinRequestByUserId(userId);
    }

    @Override
    public int countPendingJoinedCompanyRequestAndCompanyAuthorizationRequestByUser(long userId) {
        return repository.countPendingJoinedCompanyRequestAndCompanyAuthorizationRequestByUser(userId);
    }
}
