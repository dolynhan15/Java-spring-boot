package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.DurationRequest;
import org.springframework.security.core.Authentication;

public interface BusinessStatisticService {
    BaseResp saveViewProfile(Long candidateId, Long vacancyId, Long viewerId);

    BaseResp getStatisticByUserProfileId(String timeZone, Long userProfileId);
    BaseResp getCompanyDashboard(DurationRequest request, Authentication authentication);
    BaseResp getEmployeeDashboard(DurationRequest request, int page, int size, Authentication authentication);
    BaseResp getEmployeeDashboardDetail(Long staffId, DurationRequest request, Authentication authentication);
}
