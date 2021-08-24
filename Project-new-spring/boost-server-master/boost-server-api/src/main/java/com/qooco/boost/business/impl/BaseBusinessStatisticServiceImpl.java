package com.qooco.boost.business.impl;

import com.qooco.boost.business.BaseBusinessService;
import com.qooco.boost.data.enumeration.BoostApplication;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Objects;

public class BaseBusinessStatisticServiceImpl implements BaseBusinessService {

    boolean hasPermissionAccessStatisticDashboard(Authentication authentication) {
        if (BoostApplication.SELECT_APP.appId().equals(getAppId(authentication))) {
            List<String> roles = getRoles(authentication);
            return CompanyRole.ADMIN.getRolesForStatictisDashboard().stream().anyMatch(roles::contains);
        }
        return false;
    }

    void validateDateRange(Long startDate, Long endDate) {
        if (Objects.isNull(startDate) || Objects.isNull(endDate) || startDate >= endDate) {
            throw new InvalidParamException(ResponseStatus.INVALID_TIME_RANGE);
        }
    }
}
