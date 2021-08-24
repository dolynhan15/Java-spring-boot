package com.qooco.boost.business.impl.abstracts;

import com.qooco.boost.business.BaseBusinessService;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.mongo.services.base.ConversationBaseService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.dto.message.MessageDTO;
import com.qooco.boost.models.request.message.ChatMessageReq;
import com.qooco.boost.models.request.message.MediaMessageReq;
import com.qooco.boost.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.firstNonNull;

public interface BusinessChatBaseService<T extends MessageBase> extends BaseBusinessService {

    void submitMessage(Authentication auth, String conversationId, ChatMessageReq chatMessage);

    void updateMessageStatus(Authentication auth, String messageId, Integer status);

    void sendFileMessage(Authentication auth, String conversationId, MediaMessageReq mediaMessageReq);
    ConversationBaseService getConversationService();

    default void validateConversationId(String conversationId) {
        if (StringUtils.isBlank(conversationId) || !ObjectId.isValid(conversationId)) {
            throw new InvalidParamException(ResponseStatus.CHANNEL_ID_INVALID);
        }
    }

    default List<MessageDTO> convertToMessageDTO(List<T> messageDocs, long totalMessage, String timeZone, int requestSize, boolean isEncrypted, String qoocoDomainPath, String locale) {
        var messageDTOs = new ArrayList<MessageDTO>();

        var dateOfFirstItem = DateUtils.convertUTCtoLocalTimeByTimeZone(messageDocs.get(0).getCreatedDate(), timeZone);
        var startOfDate = DateUtils.atStartOfDate(dateOfFirstItem, timeZone);

        for (T messageDoc : messageDocs) {
            var dateToLocal = DateUtils.convertUTCtoLocalTimeByTimeZone(messageDoc.getCreatedDate(), timeZone);
            dateToLocal = DateUtils.atStartOfDate(dateToLocal, timeZone);
            if (dateToLocal.compareTo(startOfDate) < 0) {
                messageDTOs.add(new MessageDTO(MessageConstants.SESSION_MESSAGE, startOfDate));
                startOfDate = DateUtils.atStartOfDate(dateToLocal, timeZone);
            }

            messageDTOs.add(new MessageDTO(messageDoc, isEncrypted, qoocoDomainPath, locale));
        }

        var realSize = messageDocs.size() > requestSize ? messageDocs.size() - 1 : messageDocs.size();
        if (totalMessage > realSize) {
            messageDTOs.remove(messageDTOs.size() - 1);
        } else {
            var dateOfLastItem = DateUtils.convertUTCtoLocalTimeByTimeZone(messageDocs.get(messageDocs.size() - 1).getCreatedDate(), timeZone);
            dateOfLastItem = DateUtils.atStartOfDate(dateOfLastItem, timeZone);
            messageDTOs.add(new MessageDTO(MessageConstants.SESSION_MESSAGE, dateOfLastItem));
        }

        return messageDTOs;
    }

    default ConversationBase checkExistsConversationDoc(String conversationId) {
        ConversationBase conversationDoc = (ConversationBase) getConversationService().findById(new ObjectId(conversationId));
        if (Objects.isNull(conversationDoc)) {
            throw new InvalidParamException(ResponseStatus.NOT_FOUND_CONVERSATION);
        }
        return conversationDoc;
    }

    default void validateChatMessage(ConversationBase conversationDoc, Long senderId) {
        if (Objects.isNull(senderId)) {
            throw new InvalidParamException(ResponseStatus.NO_SENDER);
        }
        if (CollectionUtils.isEmpty(conversationDoc.getParticipants())) {
            throw new InvalidParamException(ResponseStatus.USER_IS_NOT_IN_CONVERSATION);
        }
        if (conversationDoc.isDisable()) {
            throw new InvalidParamException(ResponseStatus.CONVERSATION_IS_DISABLE);
        }
    }

    default void validateMessageInput(String messageId, Long senderId) {
        if (Objects.isNull(senderId) || StringUtils.isBlank(messageId)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        if (!ObjectId.isValid(messageId)) {
            throw new InvalidParamException(ResponseStatus.ID_INVALID);
        }
    }

    default boolean isValidMessageStatus(Integer messageStatus, int status) {
        switch (firstNonNull(messageStatus, Integer.MIN_VALUE)) {
            case MessageConstants.MESSAGE_STATUS_SENT:
                return MessageConstants.MESSAGE_STATUS_RECEIVED == status || MessageConstants.MESSAGE_STATUS_SEEN == status;
            case MessageConstants.MESSAGE_STATUS_RECEIVED:
                return MessageConstants.MESSAGE_STATUS_SEEN == status;
            case Integer.MIN_VALUE:
                return true;
            default:
                return false;
        }
    }
}
