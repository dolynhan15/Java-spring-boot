package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessChatService;
import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.business.MediaService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.Const;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.enumeration.UploadType;
import com.qooco.boost.data.mongo.embedded.FileEmbedded;
import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.data.mongo.embedded.message.AppliedMessage;
import com.qooco.boost.data.mongo.embedded.message.AppointmentDetailMessage;
import com.qooco.boost.data.mongo.embedded.message.AuthorizationMessage;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.base.ConversationBaseService;
import com.qooco.boost.data.oracle.entities.PataFile;
import com.qooco.boost.data.utils.CipherKeys;
import com.qooco.boost.enumeration.BoostHelperParticipant;
import com.qooco.boost.enumeration.ResponseStatus;
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
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.qooco.boost.constants.AttributeEventType.EVT_SEND_MESSAGE;
import static com.qooco.boost.data.constants.MessageConstants.APPLIED_STATUS_PENDING;
import static com.qooco.boost.data.constants.MessageConstants.JOIN_COMPANY_REQUEST_STATUS_PENDING;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class BusinessChatServiceImpl implements BusinessChatService {
    private final MessageDocService messageDocService;
    private final MessageCenterDocService messageCenterDocService;
    private final ConversationDocService conversationDocService;
    private final BoostActorManager boostActorManager;
    private final PushNotificationService pushNotificationService;
    private final BusinessValidatorService businessValidatorService;
    private final SendMessageToClientService sendMessageToClientService;
    private final MediaService mediaService;
    private final BusinessProfileAttributeEventService businessProfileAttributeEventService;

    @Value(ApplicationConstant.BOOST_MESSAGE_CONFIG_FILE_LIVE_TIME)
    private Integer numberLiveDayOfMessageFile;
    @Value(ApplicationConstant.BOOST_PATA_QOOCO_DOMAIN_PATH)
    private String qoocoDomainPath = "";


    @Override
    public ConversationBaseService getConversationService() {
        return conversationDocService;
    }

    @Override
    public void submitMessage(Authentication auth, String conversationId, ChatMessageReq chatMessage) {
        validateConversationId(conversationId);
        var conversationDoc = checkExistsConversationDoc(conversationId);
        this.validateChatMessage(conversationDoc, getUserId(auth));


        var messageDoc = new MessageDoc.MessageDocBuilder(conversationDoc, getUserId(auth))
                .withTextMessage(CipherKeys.decryptByAES(chatMessage.getContent(), conversationDoc.getSecretKey()))
                .build();

        if (getUserId(auth).equals(conversationDoc.getPartner(getUserId(auth)))
                || BoostHelperParticipant.getIds().contains(conversationDoc.getPartner(getUserId(auth)).getUserProfileId())) {
            messageDoc.setStatus(MessageConstants.MESSAGE_STATUS_SEEN);
        }
        messageDoc = messageDocService.save(messageDoc, true);
        ofNullable(messageDoc).orElseThrow(() -> new InvalidParamException(ResponseStatus.SAVE_FAIL));

        if (messageDoc.getReceiveInApp() == 0) {
            boostActorManager.updateReceiveInAppMessageActor(messageDoc);
        }
        sendMessageToClientService.sendMessage(messageDoc, getLocale(auth));
        pushNotificationService.notifyNewMessage(messageDoc, true);

        if (messageDoc.getSender().getUserType() == UserType.PROFILE && messageDoc.getRecipient().getUserType() != UserType.BOOST_HELPER)
            businessProfileAttributeEventService.onAttributeEvent(EVT_SEND_MESSAGE, messageDoc.getSender().getUserProfileId());
    }

    @Override
    public HistoryMessageResp getAllMessagesInPersonalConversation(Authentication auth, String conversationId, Long eventId, Long timestamp, int size) {
        if (Objects.nonNull(eventId)) {
            MessageDoc messageDoc = messageDocService.findAppointmentDetailMessage(eventId);
            conversationId = messageDoc.getConversationId().toHexString();
        }

        validateConversationId(conversationId);
        var historyMessageReq = HistoryMessageReq.builder()
                .conversationId(conversationId)
                .size(size)
                .timestamp(Objects.isNull(timestamp) ? DateUtils.toServerTimeForMongo() : new Date(timestamp))
                .timezone(getTimeZone(auth))
                .build();

        businessValidatorService.checkValidTimeZone(historyMessageReq.getTimezone());
        var conversationDoc = checkExistsConversationDoc(conversationId);
        ofNullable(conversationDoc.getParticipant(getUserId(auth))).orElseThrow(() -> new InvalidParamException(ResponseStatus.USER_IS_NOT_IN_CONVERSATION));

        return getMessageDetailByConversation(conversationDoc,
                historyMessageReq.getSize(), StringUtils.isNotBlank(getPublicKey(auth)),
                historyMessageReq.getTimestamp(), historyMessageReq.getTimezone(), getApp(auth) == SELECT_APP.value(), getLocale(auth));
    }

    private HistoryMessageResp getMessageDetailByConversation(ConversationBase conversationDoc, int size, boolean isEncrypted, Date timestamp, String timeZone, boolean isHotelApp, String locale) {
        var messageDTOs = new ArrayList<MessageDTO>();
        var messageDocs = messageDocService.findByConversationIdAndSizeAndTimestamp(conversationDoc.getId(), timestamp, size + 1);
        var hasMoreMessage = false;

        if (CollectionUtils.isNotEmpty(messageDocs)) {
            long countMessage = messageDocService.countByConversationIdAndSizeAndTimestamp(conversationDoc.getId(), timestamp);
            of(convertToMessageDTO(messageDocs, countMessage, timeZone, size, isEncrypted, qoocoDomainPath, locale)).filter(CollectionUtils::isNotEmpty).ifPresent(messageDTOs::addAll);
            hasMoreMessage = countMessage > messageDocs.size() - 1;

        }
        var vacancyStatus = getVacancyStatus(conversationDoc);
        boolean isLockedAppointmentBtn = checkVisibleAppointmentButton(conversationDoc, isHotelApp, vacancyStatus);
        boolean isLockedChatBtn = conversationDoc.isDisable() || (Objects.nonNull(vacancyStatus) && Const.Vacancy.Status.SUSPEND == vacancyStatus);

        return HistoryMessageResp.builder()
                .results(Lists.reverse(messageDTOs))
                .hasMoreMessage(hasMoreMessage)
                .isLockedAppointmentBtn(isLockedAppointmentBtn)
                .isLockedChatBtn(isLockedChatBtn)
                .build();
    }

    private boolean checkVisibleAppointmentButton(ConversationBase conversationDoc, boolean isHotelApp, Integer vacancyStatus) {
        long appointmentMessageNumber = messageDocService.countAppointmentMessage(conversationDoc.getId(), MessageConstants.APPOINTMENT_STATUS_PENDING);
        return isHotelApp && (appointmentMessageNumber > 0 ||
                (Objects.nonNull(vacancyStatus) && (Const.Vacancy.Status.SUSPEND == vacancyStatus || Const.Vacancy.Status.INACTIVE == vacancyStatus)));
    }

    private Integer getVacancyStatus(ConversationBase conversationDoc) {
        MessageCenterDoc messageCenterDoc = messageCenterDocService.findById(conversationDoc.getMessageCenterId());
        if (Objects.nonNull(messageCenterDoc) && Objects.nonNull(messageCenterDoc.getVacancy())) {
            return messageCenterDoc.getVacancy().getVacancyStatus();
        }
        return null;
    }

    @Override
    public void updateMessageStatus(Authentication auth, String messageId, Integer status) {

        validateMessageInput(messageId, getUserId(auth));
        MessageDoc foundMessage = businessValidatorService.checkExistsMessageDoc(messageId);

        MessageDoc originalMessage = new MessageDoc(foundMessage);
        // All message status == 1 (status OK : 2,3) , 2 (status OK  3) => Do Updated
        if (isValidMessageStatus(foundMessage.getStatus(), status)) {
            foundMessage.setStatus(status);
        }

        AtomicBoolean isCheckedMessageRecipient = new AtomicBoolean(true);
        switch (foundMessage.getType()) {
            case MessageConstants.APPLICANT_MESSAGE:
                ofNullable(foundMessage.getAppliedMessage())
                        .filter(it -> it.getResponseStatus() != APPLIED_STATUS_PENDING && isValidMessageStatus(it.getStatus(), status))
                        .ifPresent(it -> {
                            it.setStatus(status);
                            isCheckedMessageRecipient.set(false);
                        });
                break;
            case MessageConstants.APPOINTMENT_MESSAGE:
                ofNullable(foundMessage.getAppointmentDetailMessage())
                        .filter(it -> it.getResponseStatus() != AppointmentStatus.PENDING.getValue() && isValidMessageStatus(it.getStatus(), status))
                        .ifPresent(it -> {
                            it.setStatus(status);
                            isCheckedMessageRecipient.set(false);
                        });
                break;
            case MessageConstants.AUTHORIZATION_MESSAGE:
                ofNullable(foundMessage.getAuthorizationMessage())
                        .filter(it -> it.getResponseStatus() != JOIN_COMPANY_REQUEST_STATUS_PENDING && isValidMessageStatus(it.getStatus(), status))
                        .ifPresent(it -> {
                            it.setStatus(status);
                            isCheckedMessageRecipient.set(false);
                        });
                break;
            case MessageConstants.ASSIGNMENT_ROLE_MESSAGE:
                ofNullable(foundMessage.getStaff()).filter(it -> isValidMessageStatus(it.getStatus(), status)).ifPresent(it -> it.setStatus(status));
                break;
            default:
                break;
        }

        if (isCheckedMessageRecipient.get()) {
            if (!getUserId(auth).equals(foundMessage.getRecipient().getUserProfileId())) {
                throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
            }
        } else {
            if (!getUserId(auth).equals(foundMessage.getSender().getUserProfileId())) {
                throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
            }
        }

        if (!compareMessageStatus(originalMessage, foundMessage)) {
            foundMessage.setUpdatedDate(DateUtils.toServerTimeForMongo());
            MessageDoc saved = messageDocService.save(foundMessage, false);
            if (Objects.isNull(saved)) {
                throw new InvalidParamException(ResponseStatus.SAVE_FAIL);
            }
            if (status == MessageConstants.MESSAGE_STATUS_SEEN) {
                messageDocService.updateSeenForOlderMessage(saved, true);
            } else {
                messageDocService.updateReceivedForOlderMessage(saved, true);
            }
            sendMessageToClientService.sendMessage(saved, MessageConstants.UPDATE_MESSAGE_ACTION, getLocale(auth));
        } else {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
    }

    private boolean compareMessageStatus(MessageDoc original, MessageDoc message) {
        switch (message.getType()) {
            case MessageConstants.APPLICANT_MESSAGE:
                AppliedMessage applicantMessageOriginal = original.getAppliedMessage();
                AppliedMessage applicantMessage = message.getAppliedMessage();
                if (Objects.nonNull(applicantMessage) && Objects.nonNull(applicantMessageOriginal)) {
                    return message.getStatus() == original.getStatus() && Objects.equals(applicantMessage.getStatus(), applicantMessageOriginal.getStatus());
                }
                break;
            case MessageConstants.AUTHORIZATION_MESSAGE:
                AuthorizationMessage authMessageOriginal = original.getAuthorizationMessage();
                AuthorizationMessage authMessage = message.getAuthorizationMessage();
                if (Objects.nonNull(authMessage) && Objects.nonNull(authMessageOriginal)) {
                    return message.getStatus() == original.getStatus() && Objects.equals(authMessage.getStatus(), authMessageOriginal.getStatus());
                }
                break;
            case MessageConstants.APPOINTMENT_MESSAGE:
                AppointmentDetailMessage aptMessageOriginal = original.getAppointmentDetailMessage();
                AppointmentDetailMessage aptMessage = message.getAppointmentDetailMessage();
                if (Objects.nonNull(aptMessage) && Objects.nonNull(aptMessageOriginal)) {
                    return message.getStatus() == original.getStatus() && Objects.equals(aptMessage.getStatus(), aptMessageOriginal.getStatus());
                }
                break;
            case MessageConstants.ASSIGNMENT_ROLE_MESSAGE:
                StaffEmbedded staffOriginal = original.getStaff();
                StaffEmbedded staffMessage = message.getStaff();
                if (Objects.nonNull(staffMessage) && Objects.nonNull(staffOriginal)) {
                    return message.getStatus() == original.getStatus() && Objects.equals(staffMessage.getStatus(), staffOriginal.getStatus());
                }
                break;
            default:
                break;
        }
        return message.getStatus() == original.getStatus();
    }


    @Override
    public void sendFileMessage(Authentication auth, String conversationId, MediaMessageReq mediaMessageReq) {
        validateConversationId(conversationId);
        ConversationBase conversationDoc = checkExistsConversationDoc(conversationId);
        validateChatMessage(conversationDoc, getUserId(auth));

        PataFile pataFile = mediaService.storeMediaFileToServer(mediaMessageReq, UploadType.MEDIA_MESSAGE.toString(), auth);
        if (Objects.isNull(pataFile)) {
            throw new InvalidParamException(ResponseStatus.BAD_REQUEST);
        }

        //Create message with media file
        FileEmbedded fileMessage = new FileEmbedded(pataFile, mediaMessageReq.getClientId());
        ofNullable(numberLiveDayOfMessageFile).ifPresent(days -> fileMessage.setExpiredDate(DateUtils.addDays(new Date(), days)));

        MessageDoc messageDoc = new MessageDoc.MessageDocBuilder(conversationDoc, getUserId(auth))
                .withFileMessage(fileMessage).build();

        messageDoc = messageDocService.save(messageDoc, true);
        if (messageDoc.getReceiveInApp() == 0) {
            boostActorManager.updateReceiveInAppMessageActor(messageDoc);
        }

        sendMessageToClientService.sendMessage(messageDoc, getLocale(auth));
        pushNotificationService.notifySendFileMessage(messageDoc, true);

        if (messageDoc.getSender().getUserType() == UserType.PROFILE)
            businessProfileAttributeEventService.onAttributeEvent(EVT_SEND_MESSAGE, messageDoc.getSender().getUserProfileId());
    }
}
