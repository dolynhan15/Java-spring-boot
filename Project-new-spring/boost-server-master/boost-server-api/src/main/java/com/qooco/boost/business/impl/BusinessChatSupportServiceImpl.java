package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessChatSupportService;
import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.business.MediaService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.enumeration.BoostApplication;
import com.qooco.boost.data.enumeration.UploadType;
import com.qooco.boost.data.mongo.embedded.FileEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.SupportMessageDoc;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.mongo.services.SupportConversationDocService;
import com.qooco.boost.data.mongo.services.SupportMessageDocService;
import com.qooco.boost.data.mongo.services.base.ConversationBaseService;
import com.qooco.boost.data.oracle.entities.PataFile;
import com.qooco.boost.data.oracle.services.UserProfileService;
import com.qooco.boost.data.utils.CipherKeys;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.dto.message.MessageDTO;
import com.qooco.boost.models.request.message.ChatMessageReq;
import com.qooco.boost.models.request.message.HistoryMessageReq;
import com.qooco.boost.models.request.message.HistoryMessageResp;
import com.qooco.boost.models.request.message.MediaMessageReq;
import com.qooco.boost.socket.services.SendMessageToClientService;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static com.qooco.boost.constants.AttributeEventType.EVT_SEND_MESSAGE;
import static com.qooco.boost.enumeration.BoostHelperParticipant.BOOST_SUPPORTER;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class BusinessChatSupportServiceImpl implements BusinessChatSupportService {

    private final SupportMessageDocService supportMessageDocService;
    private final SupportConversationDocService supportConversationDocService;
    private final BusinessProfileAttributeEventService businessProfileAttributeEventService;
    private final PushNotificationService pushNotificationService;
    private final SendMessageToClientService sendMessageToClientService;
    private final BusinessValidatorService businessValidatorService;
    private final MediaService mediaService;
    private final BoostActorManager boostActorManager;
    private final UserProfileService userProfileService;

    @Value(ApplicationConstant.BOOST_MESSAGE_CONFIG_FILE_LIVE_TIME)
    private Integer numberLiveDayOfMessageFile;
    @Value(ApplicationConstant.BOOST_PATA_QOOCO_DOMAIN_PATH)
    private String qoocoDomainPath = "";

    @Override
    public ConversationBaseService getConversationService() {
        return supportConversationDocService;
    }

    @Override
    public void submitMessage(Authentication auth, String conversationId, ChatMessageReq chatMessage) {
        validateConversationId(conversationId);
        var conversation = supportConversationDocService.findById(new ObjectId(conversationId));
        ofNullable(conversation).orElseThrow(() -> new InvalidParamException(ResponseStatus.NOT_FOUND_CONVERSATION));

        long senderId = isSystemAdmin(auth) ? BOOST_SUPPORTER.id() : getUserId(auth);
        //TODO: Not support encrypt on web app now
        var content = getApp(auth) == BoostApplication.WEB_ADMIN_APP.value() ? chatMessage.getContent()
                : CipherKeys.decryptByAES(chatMessage.getContent(), conversation.getSecretKey());

        var message = new SupportMessageDoc(conversation, senderId).toBuilder()
                .createdBy(getCreatedBy(auth, conversation))
                .content(content)
                .build();

        message = supportMessageDocService.save(message, true);
        ofNullable(message).orElseThrow(() -> new InvalidParamException(ResponseStatus.SAVE_FAIL));

        if (message.getSender().getUserType() == UserType.PROFILE)
            businessProfileAttributeEventService.onAttributeEvent(EVT_SEND_MESSAGE, message.getSender().getUserProfileId());

        sendMessageToClient(message, getLocale(auth));
    }

    private UserProfileCvEmbedded getCreatedBy(Authentication auth, ConversationBase conversation) {
        return ofNullable(conversation.getParticipant(getUserId(auth)))
                .orElse(new UserProfileCvEmbedded().toBuilder()
                        .userProfileId(getUserId(auth))
                        .username(getUsername(auth))
                        .userType(UserType.SYSTEM_ADMIN).build());

    }

    @Override
    public HistoryMessageResp getAllMessagesInPersonalConversation(Authentication auth, String conversationId, Long timestamp, int size) {
        validateConversationId(conversationId);
        var historyMessageReq = HistoryMessageReq.builder()
                .conversationId(conversationId)
                .size(size)
                .timestamp(Objects.isNull(timestamp) ? DateUtils.toServerTimeForMongo() : new Date(timestamp))
                .timezone(getTimeZone(auth))
                .build();
        businessValidatorService.checkValidTimeZone(historyMessageReq.getTimezone());
        var conversation = checkExistsConversationDoc(conversationId);
        if (!isSystemAdmin(auth) && Objects.isNull(conversation.getParticipant(getUserId(auth)))) { //Only validate for normal user
            throw  new InvalidParamException(ResponseStatus.USER_IS_NOT_IN_CONVERSATION);
        }

        return getMessageDetailByConversation(conversation,
                historyMessageReq.getSize(), StringUtils.isNotBlank(getPublicKey(auth)),
                historyMessageReq.getTimestamp(), historyMessageReq.getTimezone(), getLocale(auth));

    }

    @Override
    public void updateMessageStatus(Authentication auth, String messageId, Integer status) {
        validateMessageInput(messageId, getUserId(auth));
        var foundMessage = supportMessageDocService.findById(new ObjectId(messageId));
        ofNullable(foundMessage).orElseThrow(() -> new EntityNotFoundException(ResponseStatus.NOT_FOUND_MESSAGE));

        Long senderId = foundMessage.getSender().getUserProfileId();
        if (isSystemAdmin(auth) && senderId.equals(BOOST_SUPPORTER.id())
                || !isSystemAdmin(auth) && !senderId.equals(BOOST_SUPPORTER.id())) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }

        var originalMessage = new SupportMessageDoc(foundMessage);
        // All message status == 1 (status OK : 2,3) , 2 (status OK  3) => Do Updated
        if (isValidMessageStatus(foundMessage.getStatus(), status)) {
            foundMessage.setStatus(status);
        }

        if (originalMessage.getStatus() != foundMessage.getStatus()) {
            foundMessage.setUpdatedDate(DateUtils.toServerTimeForMongo());
            var saved = supportMessageDocService.save(foundMessage, false);
            if (status == MessageConstants.MESSAGE_STATUS_SEEN) {
                supportMessageDocService.updateSeenForOlderMessage(saved);
            } else {
                supportMessageDocService.updateReceivedForOlderMessage(saved);
            }
            sendMessageToClientService.sendMessage(saved, MessageConstants.UPDATE_MESSAGE_ACTION, getLocale(auth));
        } else {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
    }

    @Override
    public void sendFileMessage(Authentication auth, String conversationId, MediaMessageReq mediaMessageReq) {
        validateConversationId(conversationId);
        ConversationBase conversationDoc = checkExistsConversationDoc(conversationId);
        validateChatMessage(conversationDoc, getUserId(auth));
        PataFile pataFile = mediaService.storeMediaFileToServer(mediaMessageReq, UploadType.MEDIA_MESSAGE.toString(), auth);
        ofNullable(pataFile).orElseThrow(() -> new InvalidParamException(ResponseStatus.BAD_REQUEST));

        //Create message with media file
        FileEmbedded fileMessage = new FileEmbedded(pataFile, mediaMessageReq.getClientId());
        ofNullable(numberLiveDayOfMessageFile).ifPresent(days -> fileMessage.setExpiredDate(DateUtils.addDays(new Date(), days)));

        long senderId = isSystemAdmin(auth) ? BOOST_SUPPORTER.id() : getUserId(auth);
        var messageDoc = new SupportMessageDoc(conversationDoc, senderId).toBuilder()
                .createdBy(getCreatedBy(auth, conversationDoc))
                .fileMessage(fileMessage)
                .type(MessageConstants.MEDIA_MESSAGE)
                .build();

        messageDoc = supportMessageDocService.save(messageDoc, true);
        if (messageDoc.getReceiveInApp() == 0) {
            boostActorManager.updateReceiveInAppMessageActor(messageDoc);
        }

        sendMessageToClient(messageDoc, getLocale(auth));

        if (messageDoc.getSender().getUserType() == UserType.PROFILE)
            businessProfileAttributeEventService.onAttributeEvent(EVT_SEND_MESSAGE, messageDoc.getSender().getUserProfileId());
    }

    private HistoryMessageResp getMessageDetailByConversation(ConversationBase conversationDoc, int size, boolean isEncrypted, Date timestamp, String timeZone, String locale) {
        var messageDTOs = new ArrayList<MessageDTO>();
        var messageDocs = supportMessageDocService.findByConversationIdAndSizeAndTimestamp(conversationDoc.getId(), timestamp, size + 1);
        var hasMoreMessage = false;

        if (CollectionUtils.isNotEmpty(messageDocs)) {
            long countMessage = supportMessageDocService.countByConversationIdAndSizeAndTimestamp(conversationDoc.getId(), timestamp);
            of(convertToMessageDTO(messageDocs, countMessage, timeZone, size, isEncrypted, qoocoDomainPath, locale)).filter(CollectionUtils::isNotEmpty).ifPresent(messageDTOs::addAll);
            hasMoreMessage = countMessage > messageDocs.size() - 1;
        }

        return HistoryMessageResp.builder()
                .results(Lists.reverse(messageDTOs))
                .hasMoreMessage(hasMoreMessage)
                .isLockedAppointmentBtn(true)
                .isLockedChatBtn(false)
                .build();
    }

    private void sendMessageToClient(MessageBase message, String locale) {
        sendMessageToClientService.sendMessage(message, locale);
        pushNotificationService.notifyNewMessage(message, true);
    }
}
