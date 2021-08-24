package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.LocationReq;
import org.springframework.security.core.Authentication;

public interface BusinessLocationService {
    BaseResp getLocation(Long companyId, Authentication authentication);

    BaseResp save(LocationReq locationReq, Authentication authentication);

    BaseResp getLocationByCompany(long companyId, int page, int size, Authentication authentication);

    BaseResp deleteLocation(long id, Authentication authentication);
}
