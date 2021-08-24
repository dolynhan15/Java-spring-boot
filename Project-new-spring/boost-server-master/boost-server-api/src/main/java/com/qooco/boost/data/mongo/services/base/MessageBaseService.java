package com.qooco.boost.data.mongo.services.base;

import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.model.ObjectLatest;
import com.qooco.boost.data.model.count.ObjectIdCount;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface MessageBaseService<T extends MessageBase> {
    List<T> findByConversationIdAndSizeAndTimestamp(ObjectId conversationId, Date timestamp, int size);

    List<T> getSentMessagesByUser(long userId, List<Integer> receiveInApps);

    List<ObjectIdCount> countUnreadMessageGroupByKey(String key, long userProfileId, int receiveInApp);

    List<ObjectIdCount> countUnreadMessageGroupByConversation(ObjectId messageCenterId, Long userProfileId, int receiveInApp);

    List<ObjectIdCount> countUnreadMessageGroupByMessageCenter(List<ObjectId> conversationIds, Long userProfileId, int receiveInApp);

    List<ObjectLatest> getLatestUpdatedDateByMessageCenterIds(List<ObjectId> messageCenterIds, Long userProfileId, int receiveInApp);

    List<ObjectLatest> getLatestUpdatedDateByConversationIds(List<ObjectId> conversationIds, Long userProfileId, int receiveInApp);

    long countByConversationIdAndSizeAndTimestamp(ObjectId conversationId, Date timestamp);

    long countUnreadMessageByUserProfileId(ObjectId conversationId, Long userProfileId, int receiveInApp);

    long countUserSendUnreadMessageByUserProfileId(Long userProfileId, int receiveInApp);

    boolean addAESSecretKeyForNoneSecretKeyMessageByConversationIds(List<ConversationDoc> conversations);

    default boolean isNotResponseMessageType(int messageType) {
        return messageType == MessageConstants.ASSIGNMENT_ROLE_MESSAGE
                || messageType == MessageConstants.TEXT_MESSAGE
                || messageType == MessageConstants.CONGRATULATION_MESSAGE
                || messageType == MessageConstants.APPOINTMENT_CANCEL_MESSAGE
                || messageType == MessageConstants.APPOINTMENT_APPLICANT_MESSAGE
                || messageType == MessageConstants.INACTIVE_VACANCY
                || messageType == MessageConstants.CHANGE_CONTACT_MESSAGE
                || messageType == MessageConstants.SUSPENDED_VACANCY
                || messageType == MessageConstants.MEDIA_MESSAGE
                || messageType == MessageConstants.BOOST_HELPER_MESSAGE;
    }
}
