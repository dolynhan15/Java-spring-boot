package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessBoostHelperService;
import com.qooco.boost.business.BusinessChatService;
import com.qooco.boost.business.BusinessLanguageService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.oracle.services.ReferralCodeService;
import com.qooco.boost.data.utils.CipherKeys;
import com.qooco.boost.enumeration.BoostHelperEventType;
import com.qooco.boost.enumeration.BoostHelperParticipant;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.rasa.UserRASAReq;
import com.qooco.boost.models.rasa.UserReferralCodeReq;
import com.qooco.boost.models.request.RASAReq;
import com.qooco.boost.models.request.message.ChatMessageReq;
import com.qooco.boost.models.transfer.BoostHelperFitTransfer;
import com.qooco.boost.models.transfer.BoostHelperProfileTransfer;
import com.qooco.boost.models.user.UserMessageReq;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.HttpHelper;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import java.util.Locale;
import java.util.Set;

import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;
import static com.qooco.boost.enumeration.BoostHelperEventType.*;
import static com.qooco.boost.enumeration.BoostHelperParticipant.BOOST_GUIDE_PROFILE;
import static com.qooco.boost.enumeration.BoostHelperParticipant.BOOST_GUIDE_SELECT;
import static java.util.Optional.ofNullable;

@Service
public class BusinessBoostHelperServiceImpl implements BusinessBoostHelperService {
    private static final String RASA_ENDPOINT_PROPERTY_PREFIX = "boost.helper.rasa.endpoint.";

    @Autowired
    private BoostActorManager boostActorManager;
    @Autowired
    private BusinessValidatorService validatorService;
    @Autowired
    private ReferralCodeService referralCodeService;
    @Autowired
    private BusinessChatService businessChatService;
    @Autowired
    private ConversationDocService conversationDocService;
    @Autowired
    private BusinessLanguageService businessLanguageService;
    @Autowired
    private Environment environment;

    @Value(ApplicationConstant.BOOST_HELPER_RASA_PATH)
    private String RASAPath = "";

    @Value("${boost.helper.rasa.locales}")
    private Set<String> supportedLocales;

    @Override
    public BaseResp getIntroduction(UserRASAReq request) {
        return emitBoostHelperMessage(request.getUserId(), BOOST_INTRODUCTION, request.getUserType(), request.getLocale());
    }

    @Override
    public BaseResp getMgsWhenShareOrRedeemReferralCode(UserReferralCodeReq req) {
        var code = req.getCode().trim().toUpperCase();
        var referralCode = referralCodeService.findActiveByOwnerAndCode(req.getUserId(), code);
        if (UserType.PROFILE == req.getUserType()) {
            return ofNullable(referralCode)
                    .map(it -> emitBoostHelperMessage(req.getUserId(), BOOST_SHARE_REFERRAL_CODE, code, req.getUserType(), req.getLocale()))
                    .or(() -> ofNullable(referralCodeService.findActiveByNotOwnerAndCode(req.getUserId(), code))
                            .map(obj -> emitBoostHelperMessage(req.getUserId(), BOOST_REDEEM_REFERRAL_CODE, code, req.getUserType(), req.getLocale())))
                    .orElseGet(() -> emitBoostHelperMessage(req.getUserId(), BOT_NOT_UNDERSTAND, req.getUserType(), req.getLocale()));
        } else {
            return emitBoostHelperMessage(req.getUserId(), BOT_NOT_UNDERSTAND, req.getUserType(), req.getLocale());
        }
    }

    @Override
    public BaseResp getMgsWhenBotNotUnderstand(UserRASAReq request) {
        return emitBoostHelperMessage(request.getUserId(), BOT_NOT_UNDERSTAND, request.getUserType(), request.getLocale());
    }

    @Override
    public BaseResp getMgsWhenBotDetectSupportKeywords(UserRASAReq request) {
        return emitBoostHelperMessage(request.getUserId(), BOT_DETECT_SUPPORT_KEYWORDS, request.getUserType(), request.getLocale());
    }

    @Override
    public BaseResp sendMessageRequestFromUserToRASA(UserMessageReq request, Authentication auth) {
        if (StringUtils.isNotBlank(request.getMessage())) {
            var user = validatorService.checkExistsUserProfile(getUserId(auth));
            var userType = 0;
            BoostHelperParticipant boostGuide = null;
            if(PROFILE_APP.appId().equals(getAppId(auth))){
                userType = UserType.PROFILE;
                boostGuide = BOOST_GUIDE_PROFILE;
            } else if(SELECT_APP.appId().equals(getAppId(auth))){
                userType = UserType.SELECT;
                boostGuide = BOOST_GUIDE_SELECT;
            }

            var sender = StringUtil.append(String.valueOf(user.getUserProfileId()), ".", String.valueOf(userType));
            var chatReq = ChatMessageReq.builder().content(request.getMessage()).build();
            businessChatService.submitMessage(auth, request.getConversationId(), chatReq);
            ConversationDoc conversationDoc = conversationDocService.findById(new ObjectId(request.getConversationId()));
            var message = CipherKeys.decryptByAES(request.getMessage(), conversationDoc.getSecretKey());
            var rasaReq = RASAReq.builder()
                    .sender(sender)
                    .message(message)
                    .build();
            try {
                HttpHelper.doPost(getRasaEndpoint(businessLanguageService.detectSupportedRasaLocale(message)), rasaReq, String.class, true);
            } catch (InternalServerError ex) {
                // ignore 500 code
            }
        }
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public String getRasaEndpoint(Locale locale) {
        return ofNullable(locale).map(it -> businessLanguageService.getSupportedSystemLocale(locale)).map(it -> environment.getProperty(RASA_ENDPOINT_PROPERTY_PREFIX + it.toLanguageTag().toLowerCase())).orElse(RASAPath);
    }

    private BaseResp emitBoostHelperMessage(Long userId, BoostHelperEventType type, int userType, Locale locale) {
        return emitBoostHelperMessage(userId, type, null, userType, locale);
    }

    private BaseResp emitBoostHelperMessage(Long userId, BoostHelperEventType type, String referralCode, int userType, Locale locale) {
        switch (userType) {
            case UserType.PROFILE:
                var userProfile = validatorService.checkExistsUserProfile(userId);
                var profileTransfer = BoostHelperProfileTransfer.builder().userProfile(userProfile).eventType(type).referralCode(referralCode).locale(locale).build();
                boostActorManager.saveBoostHelperMessageEventsInMongoForProfile(profileTransfer);
                break;
            case UserType.SELECT:
                var userFit = validatorService.checkExistsUserFit(userId);
                var fitTransfer = BoostHelperFitTransfer.builder().userFit(userFit).eventType(type).locale(locale).build();
                boostActorManager.saveBoostHelperMessageEventsInMongoForFit(fitTransfer);
                break;
            default:
                break;
        }
        return new BaseResp();
    }
}
