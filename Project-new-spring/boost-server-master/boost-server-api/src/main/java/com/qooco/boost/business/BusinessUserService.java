package com.qooco.boost.business;

import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.user.UserProfileReq;
import com.qooco.boost.models.user.UserUploadKeyReq;
import org.springframework.security.core.Authentication;

public interface BusinessUserService {

    BaseResp saveBasicUserProfile(UserProfileReq userProfileReq, Authentication authentication);

    BaseResp saveAdvancedUserProfile(UserProfileReq userProfileReq, String appId, String locale);

    BaseResp saveUserProfile(UserProfileReq userProfileReq, Authentication auth);

    BaseResp findById(Long id);

    @Deprecated
    BaseResp getBasicProfile(Long userProfileId, Authentication authentication);

    BaseResp getCareerInfo(Authentication authentication, String timeZone);

    BaseResp increaseCoin(Long userProfileId);

    BaseResp uploadPublicKey(AuthenticatedUser authenticatedUser, UserUploadKeyReq req);
}
