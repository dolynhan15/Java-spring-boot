package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.enumeration.BoostHelperEventType;
import com.qooco.boost.models.transfer.BoostHelperFitTransfer;
import com.qooco.boost.threads.services.BoostHelperService;
import com.qooco.boost.utils.MongoConverters;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.qooco.boost.data.constants.MessageConstants.RECEIVE_IN_HOTEL_APP;
import static com.qooco.boost.data.enumeration.MessageCenterType.BOOST_HELPER_CONVERSATION;
import static com.qooco.boost.enumeration.BoostHelperEventType.*;
import static com.qooco.boost.enumeration.BoostHelperParticipant.BOOST_GUIDE_SELECT;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class SaveBoostHelperEventsForFitInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SaveBoostHelperEventsForFitInMongoActor.class);
    public static final String ACTOR_NAME = "saveBoostHelperEventsForFitInMongoActor";

    private final MessageCenterDocService messageCenterDocService;
    private final MessageDocService messageDocService;
    private final BoostHelperService boostHelperService;

    private static final Map<String, Object> CHECK_EVENTS = new HashMap<>();

    @Override
    public void onReceive(Object message) {
        if (message instanceof BoostHelperFitTransfer) {
            var helperTransfer = (BoostHelperFitTransfer) message;
            var isValidEvents = Arrays.asList(BOOST_INTRODUCTION, BOT_NOT_UNDERSTAND, BOT_DETECT_SUPPORT_KEYWORDS, SELECT_FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME);
            if (Objects.isNull(helperTransfer.getEventType())) {
                var userEventKey = helperTransfer.getUserFit().getUserProfileId().toString().concat(String.valueOf(SELECT_FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME.type()));
                if (CHECK_EVENTS.putIfAbsent(userEventKey, true) == null) {
                    try {
                        saveAndSendBoostGuideMessageForOldUserNotHaveConversation(helperTransfer.getUserFit());
                    } finally {
                        CHECK_EVENTS.remove(userEventKey);
                    }
                }
            } else if (isValidEvents.contains(helperTransfer.getEventType()) && SELECT_FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME == helperTransfer.getEventType()) {
                var userEventKey = helperTransfer.getUserFit().getUserProfileId().toString().concat(String.valueOf(SELECT_FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME.type()));
                if (CHECK_EVENTS.putIfAbsent(userEventKey, true) == null) {
                    try {
                        saveAndSendBoostGuideMessage(helperTransfer.getUserFit(), SELECT_FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME);
                    } finally {
                        CHECK_EVENTS.remove(userEventKey);
                    }
                }
                logger.info("Boost Helper Event User Fit finishes Basic profile ");
            } else if (isValidEvents.contains(helperTransfer.getEventType())) {
                saveAndSendBoostGuideMessageRepeatable(helperTransfer);
            }
        }
    }

    private void saveAndSendBoostGuideMessageForOldUserNotHaveConversation(UserFit userFit) {
        var existedMessageCenter = messageCenterDocService.findByTypeAndUserOfBoostHelper(BOOST_HELPER_CONVERSATION.value(), userFit.getUserProfileId(), UserType.SELECT);
        if (Objects.isNull(existedMessageCenter))
            saveAndSendBoostGuideMsg(userFit, SELECT_FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME, null);
    }

    private void saveAndSendBoostGuideMessageRepeatable(BoostHelperFitTransfer boostHelperTransfer) {
        saveAndSendBoostGuideMsg(boostHelperTransfer.getUserFit(), boostHelperTransfer.getEventType(), boostHelperTransfer.getLocale());
    }

    private void saveAndSendBoostGuideMessage(UserFit user, BoostHelperEventType eventType) {
        var existedMessage = messageDocService.findBoostHelperMessageBySenderAndReceiverAndEventType(BOOST_GUIDE_SELECT.id(), user.getUserProfileId(), eventType.type());
        if (CollectionUtils.isEmpty(existedMessage)) saveAndSendBoostGuideMsg(user, eventType, null);
    }

    private void saveAndSendBoostGuideMsg(UserFit receiver, BoostHelperEventType eventType, Locale locale) {
        var recipient = MongoConverters.convertToUserProfileCvMessageEmbedded(receiver);
        var doc = boostHelperService.createMessageDoc(recipient, eventType, RECEIVE_IN_HOTEL_APP, locale);
        boostHelperService.saveAndSendMessage(doc, null);
    }
}
