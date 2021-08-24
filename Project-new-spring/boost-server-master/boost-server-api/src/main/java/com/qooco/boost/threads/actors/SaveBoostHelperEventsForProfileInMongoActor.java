package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.mongo.embedded.AssessmentFullEmbedded;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.entities.UserQualification;
import com.qooco.boost.data.oracle.services.AssessmentService;
import com.qooco.boost.data.oracle.services.UserLanguageService;
import com.qooco.boost.data.oracle.services.UserProfileService;
import com.qooco.boost.data.oracle.services.UserQualificationService;
import com.qooco.boost.enumeration.BoostHelperEventType;
import com.qooco.boost.models.transfer.BoostHelperProfileTransfer;
import com.qooco.boost.threads.services.BoostHelperService;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.qooco.boost.data.constants.MessageConstants.RECEIVE_IN_CAREER_APP;
import static com.qooco.boost.data.enumeration.MessageCenterType.BOOST_HELPER_CONVERSATION;
import static com.qooco.boost.enumeration.BoostHelperEventType.*;
import static com.qooco.boost.enumeration.BoostHelperParticipant.BOOST_GUIDE_PROFILE;
import static com.qooco.boost.enumeration.ProfileStep.EMPTY_PROFILE_STEP;
import static com.qooco.boost.enumeration.ProfileStep.QUALIFICATION_STEP;
import static java.util.Optional.ofNullable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class SaveBoostHelperEventsForProfileInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SaveBoostHelperEventsForProfileInMongoActor.class);
    public static final String ACTOR_NAME = "saveBoostHelperEventsForProfileInMongoActor";

    private final UserProfileService userProfileService;
    private final UserQualificationService qualificationService;
    private final UserLanguageService userLanguageService;
    private final AssessmentService assessmentService;
    private final MessageCenterDocService messageCenterDocService;
    private final MessageDocService messageDocService;
    private final BoostHelperService boostHelperService;

    @Value(ApplicationConstant.BOOST_PATA_CERTIFICATION_PERIOD)
    private int expiredDays;

    private static final int LIMIT_24_HOURS_MILLISECOND = 24 * 60 * 60 * 1000;
    private static final Map<String, Object> CHECK_EVENTS = new HashMap<>();

    @Override
    public void onReceive(Object message) {
        if (message instanceof UserProfile) {
            var userProfile = ((UserProfile) message);
            var userEventKey = userProfile.getUserProfileId().toString().concat(String.valueOf(FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME.type()));
            if (CHECK_EVENTS.putIfAbsent(userEventKey, true) == null) {
                try {
                    saveAndSendBoostGuideMessage(userProfile, FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME);
                } finally {
                    CHECK_EVENTS.remove(userEventKey);
                }
            }
            logger.info("Boost Helper Event User Profile finishes Basic profile ");
        } else if (message instanceof Long) {
            boostGuideMessageForFinishUserProfile((Long) message);
        } else if (message instanceof BoostHelperProfileTransfer) {
            var helperTransfer = (BoostHelperProfileTransfer) message;
            var isValidEvents = Arrays.asList(BOOST_INTRODUCTION, BOOST_SHARE_REFERRAL_CODE, BOOST_REDEEM_REFERRAL_CODE, BOT_NOT_UNDERSTAND, BOT_DETECT_SUPPORT_KEYWORDS);
            if (isValidEvents.contains(helperTransfer.getEventType()))
                saveAndSendBoostGuideMessageRepeatable(helperTransfer);
        }
    }

    private void boostGuideMessageForFinishUserProfile(Long userProfileId) {
        var userProfile = userProfileService.findById(userProfileId);
        BoostHelperEventType[] eventTypeForProfileStep = {
                FINISH_BASIC_PROFILE_NOT_RETURN_APP_FOR_24_HOURS,
                FINISH_ADVANCED_PROFILE_NOT_RETURN_APP_FOR_24_HOURS,
                FINISH_JOB_PROFILE_NOT_RETURN_APP_FOR_24_HOURS,
                ADD_JOB_EXPERIENCE_NOT_RETURN_APP_FOR_24_HOURS,
                FILL_PERSONAL_INFORMATION_NOT_RETURN_APP_FOR_24_HOURS};

        var isLargerThan24Hours = (new Date().getTime()) - userProfile.getUpdatedDate().getTime() > LIMIT_24_HOURS_MILLISECOND;
        var profileStep = firstNonNull(userProfile.getProfileStep(), EMPTY_PROFILE_STEP.value);
        if (isLargerThan24Hours && profileStep > EMPTY_PROFILE_STEP.value && profileStep < QUALIFICATION_STEP.value && profileStep <= eventTypeForProfileStep.length) {
            var userEventKey = userProfileId.toString().concat(String.valueOf(eventTypeForProfileStep[profileStep - 1].type()));
            if (CHECK_EVENTS.putIfAbsent(userEventKey, true) == null) {
                try {
                    saveAndSendBoostGuideMessage(userProfile, eventTypeForProfileStep[profileStep - 1]);
                } finally {
                    CHECK_EVENTS.remove(userEventKey);
                }
            }
        } else if (profileStep > EMPTY_PROFILE_STEP.value) {
            var userEventKey = userProfileId.toString().concat(String.valueOf(FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME.type()));
            if (CHECK_EVENTS.putIfAbsent(userEventKey, true) == null) {
                try {
                    saveAndSendBoostGuideMessageForOldUserNotHaveConversation(userProfile);
                } finally {
                    CHECK_EVENTS.remove(userEventKey);
                }
            }
        }
        ofNullable(qualificationService.findByUserProfileId(userProfileId)).ifPresent(it -> it.stream().filter(this::calculateUserQualificationExpiresIn3Days)
                .forEach(qualification -> saveAndSendBoostGuideMessage(userProfile, USER_QUALIFICATION_EXPIRES_IN_3_DAYS, qualification)));
    }

    private void saveAndSendBoostGuideMessageForOldUserNotHaveConversation(UserProfile userProfile) {
        var existedMessageCenter = messageCenterDocService.findByTypeAndUserOfBoostHelper(BOOST_HELPER_CONVERSATION.value(), userProfile.getUserProfileId(), UserType.PROFILE);
        if (Objects.isNull(existedMessageCenter))
            saveAndSendBoostGuideMsg(userProfile, FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME);
    }

    private void saveAndSendBoostGuideMessageRepeatable(BoostHelperProfileTransfer boostHelperTransfer) {
        saveAndSendBoostGuideMsg(boostHelperTransfer.getUserProfile(), boostHelperTransfer.getEventType(), null, boostHelperTransfer.getReferralCode(), boostHelperTransfer.getLocale());
    }

    private void saveAndSendBoostGuideMessage(UserProfile userProfile, BoostHelperEventType eventType) {
        saveAndSendBoostGuideMessage(userProfile, eventType, null);
    }

    private void saveAndSendBoostGuideMessage(UserProfile userProfile, BoostHelperEventType eventType, UserQualification qualification) {
        var existedMessage = Objects.isNull(qualification)
                ? messageDocService.findBoostHelperMessageBySenderAndReceiverAndEventType(BOOST_GUIDE_PROFILE.id(), userProfile.getUserProfileId(), eventType.type())
                : messageDocService.findBoostHelperMessageBySenderAndReceiverAndEventType(BOOST_GUIDE_PROFILE.id(), userProfile.getUserProfileId(), eventType.type(), qualification.getId());
        if (CollectionUtils.isEmpty(existedMessage))
            saveAndSendBoostGuideMsg(userProfile, eventType, qualification, null, null);
    }

    private void saveAndSendBoostGuideMsg(UserProfile receiver, BoostHelperEventType eventType) {
        saveAndSendBoostGuideMsg(receiver, eventType, null, null, null);
    }

    private void saveAndSendBoostGuideMsg(UserProfile receiver, BoostHelperEventType eventType, UserQualification qualification, String referralCode, Locale locale) {
        var userLanguages = userLanguageService.findByUserProfileId(receiver.getUserProfileId());
        receiver.setUserLanguageList(userLanguages);
        var recipient = MongoConverters.convertToUserProfileCvMessageEmbedded(receiver);

        var doc = boostHelperService.createMessageDoc(recipient, eventType, RECEIVE_IN_CAREER_APP, locale);
        var assessment = (eventType == USER_QUALIFICATION_EXPIRES_IN_3_DAYS && Objects.nonNull(qualification)) ?
                new AssessmentFullEmbedded(assessmentService.findById(qualification.getAssessmentId()), qualification.getId()) : null;
        doc.getBoostHelperMessage().setAssessment(assessment).setReferralCode(referralCode);
        boostHelperService.saveAndSendMessage(doc, null);
    }

    private boolean calculateUserQualificationExpiresIn3Days(UserQualification qualification) {
        var expiredTime = DateUtils.addDays(DateUtils.getUtcForOracle(qualification.getSubmissionTime()), expiredDays).getTime();
        var nowTime = new Date().getTime();
        return expiredTime > nowTime && Math.abs(expiredTime - nowTime) <= 3 * LIMIT_24_HOURS_MILLISECOND;
    }
}
