package com.qooco.boost.business;

import com.qooco.boost.enumeration.ProfileStep;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.PageRequest;
import org.springframework.security.core.Authentication;

import java.util.concurrent.Future;

public interface BusinessProfileAttributeEventService extends BaseBusinessService {
    void onUserProfileStep(Authentication authentication, ProfileStep profileStep);
    void onUserProfileStep(Long userProfileId, ProfileStep profileStep);
    void onEnterAppEvent(Long userProfileId);
    void onAttributeEvent(int eventCode, Authentication authentication);
    void onAttributeEvent(int eventCode, Long userProfileId);
    Future<Object> onAttributeEventAsync(int eventCode, Authentication authentication);
    Future<Object> onAttributeEventAsync(int eventCode, Long userProfileId);
    BaseResp getProfileAttributeEvent(Authentication authentication, Long userProfileId, boolean hasLevel, PageRequest pageRequest);
    BaseResp getAttributeLevelUp(Authentication authentication);
}
