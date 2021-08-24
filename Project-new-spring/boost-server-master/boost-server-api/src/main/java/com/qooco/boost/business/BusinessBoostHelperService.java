package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.rasa.UserRASAReq;
import com.qooco.boost.models.rasa.UserReferralCodeReq;
import com.qooco.boost.models.user.UserMessageReq;
import org.springframework.security.core.Authentication;

import java.util.Locale;

public interface BusinessBoostHelperService extends BaseBusinessService{
    BaseResp getIntroduction(UserRASAReq request);
    BaseResp getMgsWhenShareOrRedeemReferralCode(UserReferralCodeReq userReferralCode);
    BaseResp getMgsWhenBotNotUnderstand(UserRASAReq request);
    BaseResp getMgsWhenBotDetectSupportKeywords(UserRASAReq request);
    BaseResp sendMessageRequestFromUserToRASA(UserMessageReq request, Authentication authentication);
    String getRasaEndpoint(Locale locale);
}