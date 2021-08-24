package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.enumeration.ContactPersonStatus;
import com.qooco.boost.data.model.ObjectIdList;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.services.base.ConversationBaseService;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public interface ConversationDocService extends ConversationBaseService<ConversationDoc, ObjectId> {
    ConversationDoc save(MessageCenterDoc messageCenterDoc, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv);

    ConversationDoc save(MessageCenterDoc messageCenterDoc, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv, int contactPersonStatus);

    List<ConversationDoc> findByLimit(List<ObjectId> excludeIds, int limit);

    List<ConversationDoc> findByMessageCenterId(ObjectId messageCenterId);

    ConversationDoc findByMessageCenterIdAndSenderAndRecipient(ObjectId messageCenterDocId, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv);

    List<ConversationDoc> findByMessageCenterIdAndUserProfileId(List<ObjectId> messageCenterIds, Long userProfile);

    List<ConversationDoc> findByMessageCenterIdAndUserProfileId(ObjectId messageCenterId, Long userProfileId);

    List<ConversationDoc> findByMessageCenterAndParticipant(ObjectId messageCenterId, List<List<Long>> userProfileGroup);

    List<ConversationDoc> findNonSecretKeyConversation(Long userId);

    List<ObjectIdList> getConversationsGroupByMessageCenter(Long company, Long userProfile);

    void setDisableConversations(Long companyId, Long userProfileId, CompanyRole newRole, CompanyRole oldRole);

    void setChangedContactPersonStatus(Long companyId, Long oldUserId, CompanyRole newRole, CompanyRole oldRole);

    void setChangedContactPersonStatus(Long id, ContactPersonStatus contactPersonStatus);

    void setDisableConversations(List<ObjectId> messageCenterIds, Long userProfileId, boolean isDisable);

    void setDisableConversations(List<ObjectId> ids, boolean isDisable);

    void deleteConversation(List<ObjectId> id);

    void deleteConversation(ObjectId messageCenterId, Long userProfileId);

    void updateParticipant(Map<ObjectId, List<UserProfileCvEmbedded>> participants);

    void updateCreatedBy(Map<ObjectId, UserProfileCvEmbedded> createdBy);

    void syncIsDeletedMessageInConversationDoc();
}
