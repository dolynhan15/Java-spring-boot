package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.model.StaffStatistic;
import com.qooco.boost.data.oracle.entities.Staff;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StaffService {

    int countPendingCompanyOfAdmin(long adminId);

    Staff save(Staff staff);

    Staff findById(Long id);

    Staff findByIdNotCheckDeleted(Long id);

    List<Staff> findById(List<Long> ids);

    List<Staff> findByIdsAndCompanyIdAndRoleNames(Long companyId, List<Long> ids, List<String> roleNames);

    Staff findByPendingCompanyAndAdmin(long companyId, long adminId);

    List<Staff> findStaffOfCompanyByRole(long companyId, long roleId);

    List<Staff> findByUserProfileAndStatus(long userProfileId, ApprovalStatus companyStatus);

    Page<Staff> findStaffOfCompany(long companyId, int page, int size);

    Page<Staff> findStaffOfCompany(long companyId, List<Long> ignoreUserIds, int page, int size);

    Page<Staff> findStaffByCompanyExceptOwner(long companyId, long userProfileId, int page, int size);

    List<Staff> findByUserProfileAndCompanyApproval(long userProfileId, long companyId);

    List<Staff> findByUserProfileAndCompany(Long companyId, Long userProfileId);

    List<Staff> findByUserProfileAndCompany(Long companyId, List<Long> userProfileIds);

    Boolean exists(Long id);

    List<Staff> findByCompanyApprovalAndAdmin(long companyId, long adminId);

    List<Staff> findByRole(long roleId);

    List<Staff> findByCompanyAndAdmin(long companyId, long adminId);

    Boolean checkStaffRoles(long companyId, Long userProfileId, List<String> roleCompanies);

    Page<Staff> findCompanyStaffsByRolesExceptOwner(long companyId, long userProfileId, List<String> roles, int page, int size);

    Page<Staff> findCompanyStaffsByRoles(long companyId, List<String> roles, int page, int size);

    Staff findByStaffIdAndCompany(long staffId, long companyId);

    int countByUserProfileAndCompany(long userProfileId, long companyId);

    List<Staff> findByUserProfileAndCompanyApprovalAndRoles(Long userProfileId, Long companyId, List<String> roles);

    List<Staff> findByCompanyIds(List<Long> companyIds);

    int countByCompany(Long companyId);

    Page<StaffStatistic> findStaffStatisticByCompany(long companyId,  long startDate, long beginningOfEndDate,
                                                     long endDate, int page, int size);
}
