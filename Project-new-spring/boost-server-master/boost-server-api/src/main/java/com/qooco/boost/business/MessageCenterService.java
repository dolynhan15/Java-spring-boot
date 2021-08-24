package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import org.springframework.security.core.Authentication;

public interface MessageCenterService extends BaseBusinessService {

    BaseResp findByMessageCenterTypeAndUserProfileFromCareerById(Authentication authentication, String id, Boolean isGroup);

    BaseResp findByMessageCenterTypeAndUserProfileFromCareerWithTimestamp(Authentication authentication, Boolean isGroup, Long timestamp, int size);

    BaseResp findByUserProfileFromHotelById(Authentication authentication, String id);

    BaseResp findByUserProfileFromHotelWithTimestamp(Authentication authentication, Long timestamp, int size);

    BaseResp delete(Authentication authentication, String id, boolean isHotelApp);
}
