
package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.user.UserInfoDTO;
import com.qooco.boost.models.qooco.SocialLoginReq;
import com.qooco.boost.models.qooco.SocialLoginWithClientInfoReq;
import com.qooco.boost.models.request.authorization.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface BusinessAuthorizationService extends BaseBusinessService {
    BaseResp signUpWithSystem(SignUpSystemReq signUpSystemReq, String appId);

    BaseResp signUpByQoocoSystem(SignUpSystemReq signUpSystemReq, String appId);

    BaseResp<UserInfoDTO> signUpWithSystemAndClientInfo(SignUpSystemWithClientInfoReq signUpSystemReq);

    BaseResp doGenerateCode(GenerateCodeReq request);

    BaseResp doVerifyCode(VerifyCodeReq verifyCodeReq);

    @Deprecated
    BaseResp loginWithSystemAccount(String username, String password, String appId);

    BaseResp loginWithSystemAccountWithClientInfo(String username, String password, ClientInfoReq clientInfo, String appId);

    BaseResp socialLogin(SocialLoginReq loginReq);

    BaseResp socialLoginWithClientInfo(SocialLoginWithClientInfoReq req);

    BaseResp doForgotPassword(ForgotPasswordReq forgotPasswordReq);

    BaseResp doLogoutWithSystemAccount(Authentication authentication);

    BaseResp updateChannelId(String channelId, Authentication authentication);

    BaseResp updateClientInfo(ClientInfoReq clientInfoReq, String token);
}