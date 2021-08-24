package com.qooco.boost.threads.services.impl;

import com.google.common.collect.ImmutableList;
import com.qooco.boost.business.BusinessLanguageService;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.mongo.embedded.BoostHelperEmbedded;
import com.qooco.boost.data.mongo.embedded.ButtonEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.embedded.message.BoostHelperMessage;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.localization.BaseLocaleDoc;
import com.qooco.boost.data.mongo.entities.localization.boost.BoostEnUsDoc;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.localization.boost.BoostLocaleService;
import com.qooco.boost.enumeration.BoostHelperEventType;
import com.qooco.boost.enumeration.BoostHelperParticipant;
import com.qooco.boost.socket.services.SendMessageToClientService;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.threads.services.BoostHelperService;
import com.qooco.boost.threads.services.MessageCenterDocActorService;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.qooco.boost.data.constants.MessageConstants.*;
import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.enumeration.BoostHelperParticipant.BOOST_GUIDE_PROFILE;
import static com.qooco.boost.enumeration.BoostHelperParticipant.BOOST_GUIDE_SELECT;
import static java.util.Arrays.stream;
import static java.util.Locale.US;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

@Service
public class BoostHelperServiceImpl implements BoostHelperService {

    @Autowired
    private BoostLocaleService boostLocaleService;
    @Autowired
    private SendMessageToClientService sendMessageToClientService;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    protected MessageCenterDocActorService messageCenterDocActorService;
    @Autowired
    protected ConversationDocService conversationDocService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private BusinessLanguageService businessLanguageService;

    @Override
    public void saveAndSendMessage(MessageDoc doc, String locale) {
        doc = messageDocService.save(doc, true);
        sendMessageAndPushNotificationToClient(doc, locale);
    }

    private void sendMessageAndPushNotificationToClient(MessageDoc message, String locale) {
        sendMessageToClientService.sendMessage(message, locale);
        if (TEXT_MESSAGE == message.getType()) {
            pushNotificationService.notifyNewMessage(message, true);
        } else {
            pushNotificationService.notifyBoostHelperMessage(message, true);
        }
    }

    @Override
    public MessageDoc createMessageDoc(UserProfileCvMessageEmbedded recipient, BoostHelperEventType eventType, int receiveInApp) {
        return createMessageDoc(recipient, eventType, receiveInApp, null);
    }

    @Override
    public MessageDoc createMessageDoc(UserProfileCvMessageEmbedded recipient, BoostHelperEventType eventType, int receiveInApp, Locale locale) {
        var messageCenter = messageCenterDocActorService.saveForBoostHelper(createBoostHelperMessageCenter(receiveInApp == PROFILE_APP.value()), recipient);
        var boostHelperSender = createBoostHelperParticipant(receiveInApp == PROFILE_APP.value() ? BOOST_GUIDE_PROFILE : BOOST_GUIDE_SELECT);
        if (Objects.isNull(locale)) {
            var lastMessage = messageDocService.findLastMessageBySenderIdAndBot(recipient.getUserProfileId(), boostHelperSender.getUserProfileId());
            locale = ofNullable(lastMessage)
                    .map(it -> businessLanguageService.detectSupportedRasaLocale(lastMessage.getContent()))
                    .map(businessLanguageService::getSupportedSystemLocale)
                    .orElse(US);
        }

        var doc = new MessageDoc(MessageDoc.builder()
                .messageCenterId(ofNullable(messageCenter).map(MessageCenterDoc::getId).orElse(new ObjectId()))
                .conversationId(ofNullable(messageCenter).map(it -> conversationDocService.save(it, boostHelperSender, recipient).getId()).orElse(new ObjectId()))
                .sender(boostHelperSender)
                .recipient(recipient)
                .receiveInApp(receiveInApp)
                .status(MESSAGE_STATUS_SENT)
                .type(BOOST_HELPER_MESSAGE)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build());

        Class<?> clazzOfLocale;
        try {
            clazzOfLocale = Class.forName(BoostEnUsDoc.class.getPackageName() + ".Boost" +
                    stream(locale.toLanguageTag().split("-"))
                            .map(String::toLowerCase)
                            .map(StringUtils::capitalize)
                            .collect(joining()) +
                    "Doc");
        } catch (ClassNotFoundException ex) {
            clazzOfLocale = BoostEnUsDoc.class;
        }

        var clazz = clazzOfLocale;
        var textLocale = ofNullable(boostLocaleService.findById(eventType.messageKey(), clazz)).map(BaseLocaleDoc::content).orElse(null);
        var boostHelperMgs = new BoostHelperMessage().setEventType(eventType.type());
        if (eventType.actionButtons().length == 0) {
            doc.setType(TEXT_MESSAGE);
            doc.setContent(textLocale);
        } else {
            var btn = stream(eventType.actionButtons())
                    .map(it -> ButtonEmbedded.builder()
                            .id(it.id())
                            .name(ofNullable(boostLocaleService.findById(it.btnName(), clazz)).map(BaseLocaleDoc::content).orElse(null))
                            .build())
                    .collect(Collectors.toList());
            boostHelperMgs.setButtons(btn).setText(textLocale);
        }
        doc.setBoostHelperMessage(boostHelperMgs);
        return doc;
    }

    private BoostHelperEmbedded createBoostHelperMessageCenter(boolean isProfileApp) {
        var boostGuide = isProfileApp ? BOOST_GUIDE_PROFILE : BOOST_GUIDE_SELECT;
        var textMap = boostLocaleService.findById(ImmutableList.of(boostGuide.nameKey(), boostGuide.descriptionKey())).stream()
                .collect(Collectors.toMap(BaseLocaleDoc::id, it -> it));
        return BoostHelperEmbedded.builder()
                .name(ofNullable(textMap.get(boostGuide.nameKey())).map(BaseLocaleDoc::content).orElse(""))
                .description(ofNullable(textMap.get(boostGuide.descriptionKey())).map(BaseLocaleDoc::content).orElse(""))
                .avatar(boostGuide.avatar())
                .build();
    }

    private UserProfileCvMessageEmbedded createBoostHelperParticipant(BoostHelperParticipant participant) {
        var textMap = boostLocaleService.findById(ImmutableList.of(participant.nameKey(), participant.descriptionKey())).stream()
                .collect(Collectors.toMap(BaseLocaleDoc::id, it -> it));

        var sender = new UserProfileCvMessageEmbedded();
        sender.setUserProfileId(participant.id());
        sender.setUserType(UserType.BOOST_HELPER);
        sender.setFirstName(ofNullable(textMap.get(participant.nameKey())).map(BaseLocaleDoc::content).orElse(""));
        sender.setDescription(ofNullable(textMap.get(participant.descriptionKey())).map(BaseLocaleDoc::content).orElse(""));
        sender.setAvatar(participant.avatar());
        return sender;
    }
}
