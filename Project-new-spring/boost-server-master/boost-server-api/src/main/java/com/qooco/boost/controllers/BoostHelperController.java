package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessBoostHelperService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.rasa.UserRASAReq;
import com.qooco.boost.models.rasa.UserReferralCodeReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.qooco.boost.constants.URLConstants.PROFILE_PATH;
import static com.qooco.boost.constants.URLConstants.RASA;
import static com.qooco.boost.enumeration.BoostHelperEventType.*;
import static com.qooco.boost.enumeration.ResponseStatus.SUCCESS;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.Integer.parseInt;
import static java.util.Locale.forLanguageTag;
import static org.apache.commons.collections4.MapUtils.getMap;

@Api(tags = "Boost Helper")
@RestController
@RequestMapping(URLConstants.BOOST_HELPER)
public class BoostHelperController extends BaseController {
    private static final String RASA_RECIPIENT_ID = "recipient_id";
    private static final String RASA_CUSTOM_DATA_KEY = "custom";
    private static final String RASA_RESP_TYPE_KEY = "type";
    private static final String RASA_RESP_REFER_CODE_KEY = "referral_code";

    @Autowired
    private BusinessBoostHelperService businessBoostHelperService;

    @ApiOperation(value = "Send message response from RASA to user", httpMethod = "POST", response = BaseResp.class, notes = note)
    @PostMapping(RASA + PROFILE_PATH)
    @PreAuthorize("@boostSecurity.isRasaCallback(#token)")
    public Object sendMessageResponseFromRASAToUser(@RequestParam String token, @RequestParam("locale") String localeId, @RequestBody Map<String, Object> body) {
        var user = body.getOrDefault(RASA_RECIPIENT_ID, "").toString();
        var customData = getMap(body, RASA_CUSTOM_DATA_KEY, Map.of());
        var locale = forLanguageTag(localeId);
        var type = parseInt(firstNonNull(customData.get(RASA_RESP_TYPE_KEY), MIN_VALUE).toString());
        if (type == BOOST_INTRODUCTION.type())
            return getBoostHelperIntroduction(new UserRASAReq(user, locale));
        if (type == BOT_NOT_UNDERSTAND.type())
            return getBoostHelperMessageWhenBotNotUnderstand(new UserRASAReq(user, locale));
        if (type == BOOST_SHARE_REFERRAL_CODE.type())
            return getBoostHelperMessageWhenShareOrRedeemReferralCode((UserReferralCodeReq) new UserReferralCodeReq().setCode(firstNonNull(customData.get(RASA_RESP_REFER_CODE_KEY), "").toString()).setUser(user).setLocale(locale));
        if (type == BOT_DETECT_SUPPORT_KEYWORDS.type())
            return getBoostHelperMessageWhenBotDetectSupportKeywords(new UserRASAReq(user, locale));
        return success(new BaseResp(SUCCESS));
    }

    private Object getBoostHelperIntroduction(@RequestBody UserRASAReq req) {
        BaseResp result = businessBoostHelperService.getIntroduction(req);
        return success(result);
    }

    private Object getBoostHelperMessageWhenShareOrRedeemReferralCode(@RequestBody UserReferralCodeReq userReferralCodeReq) {
        BaseResp result = businessBoostHelperService.getMgsWhenShareOrRedeemReferralCode(userReferralCodeReq);
        return success(result);
    }

    private Object getBoostHelperMessageWhenBotNotUnderstand(@RequestBody UserRASAReq req) {
        BaseResp result = businessBoostHelperService.getMgsWhenBotNotUnderstand(req);
        return success(result);
    }

    private Object getBoostHelperMessageWhenBotDetectSupportKeywords(@RequestBody UserRASAReq req) {
        BaseResp result = businessBoostHelperService.getMgsWhenBotDetectSupportKeywords(req);
        return success(result);
    }

    private final String note = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE;
}
