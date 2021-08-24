package com.qooco.boost.business;

import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.user.FitUserReq;
import com.qooco.boost.models.user.UserUploadKeyReq;
import org.springframework.security.core.Authentication;

public interface BusinessFitUserService {

    BaseResp saveBasicFitUser(FitUserReq fitUserReq, Authentication authentication);

    BaseResp saveAdvancedFitUser(FitUserReq fitUserReq, Authentication authentication);

    BaseResp getRecruiterInfo(Authentication authentication);

    BaseResp uploadPublicKey(AuthenticatedUser authenticatedUser, UserUploadKeyReq req);
}
