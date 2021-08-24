package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.AppVersionReq;
import org.springframework.security.core.Authentication;

public interface BusinessAppVersionService {
    BaseResp getLatestVersion(Integer appVersion, String appId, String os);
    BaseResp saveAppVersion(AppVersionReq request, Authentication authentication);
    BaseResp getAll(Authentication authentication);

}
