package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.ActiveTime;
import com.qooco.boost.models.request.RoleAssignedReq;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface BusinessStaffService extends BaseBusinessService {
    BaseResp getContactPersons(long companyId, long userProfileId, int page, int size, Authentication authentication);

    BaseResp getCompanyRolesByUser(long companyId, Long userProfileId, Authentication authentication);

    BaseResp findStaffsByRoleCompany(long companyId, Long userProfileId, int page, int size, Authentication authentication);

    BaseResp getCompanyStaffs(Authentication authentication, Long companyId, boolean isExcludedMe, int page, int size);

    BaseResp getCompanyStaffs(Authentication authentication, boolean isExcludedMe, int page, int size);

    BaseResp setRoleStaffOfCompany(RoleAssignedReq req, Authentication authentication);

    @Deprecated
    BaseResp deleteStaff(Long userProfileId, long companyId, long staffId);

    BaseResp deleteStaff(Authentication authentication, long staffId);

    BaseResp saveStaffWorking(Authentication authentication, List<ActiveTime> request);
}
