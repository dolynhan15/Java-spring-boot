package com.qooco.boost.data.mongo.services.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.enumeration.doc.MessageDocEnum;
import com.qooco.boost.data.model.ObjectLatest;
import com.qooco.boost.data.model.count.ObjectIdCount;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.SupportMessageDoc;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.mongo.repositories.SupportMessageDocRepository;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.data.mongo.services.SupportConversationDocService;
import com.qooco.boost.data.mongo.services.SupportMessageDocService;
import com.qooco.boost.data.mongo.services.impl.abstracts.MessageDocAbstract;
import com.qooco.boost.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SupportMessageDocServiceImpl extends MessageDocAbstract<SupportMessageDoc> implements SupportMessageDocService {

    private final SupportMessageDocRepository repository;
    private final SupportConversationDocService supportConversationDocService;
    private final MessageCenterDocService messageCenterDocService;

    @Override
    public SupportMessageDoc save(SupportMessageDoc messageDoc, boolean isEditUpdatedDate) {
        var now = DateUtils.toServerTimeForMongo();
        if (isEditUpdatedDate) {
            supportConversationDocService.updateNowForUpdatedDate(Lists.newArrayList(messageDoc.getConversationId()), now);
            messageCenterDocService.updateNowForUpdatedDate(Lists.newArrayList(messageDoc.getMessageCenterId()), now);
        }
        return repository.save(messageDoc);
    }

    @Override
    public SupportMessageDoc findById(ObjectId id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<SupportMessageDoc> findByConversationIdAndSizeAndTimestamp(ObjectId conversationId, Date timestamp, int size) {
        return super.findByConversationIdAndSizeAndTimestamp(conversationId, timestamp, size);
    }

    @Override
    public long countByConversationIdAndSizeAndTimestamp(ObjectId conversationId, Date timestamp) {
        return super.countByConversationIdAndSizeAndTimestamp(conversationId, timestamp);
    }

    @Override
    public void updateSeenForOlderMessage(SupportMessageDoc message) {
        updateStatusForOlderActionMessage(message, UNREAD_STATUS, MessageConstants.MESSAGE_STATUS_SEEN);
    }

    @Override
    public void updateReceivedForOlderMessage(SupportMessageDoc message) {
        updateStatusForOlderActionMessage(message, List.of(MessageConstants.MESSAGE_STATUS_SENT), MessageConstants.MESSAGE_STATUS_SEEN);
    }

    @Override
    public long countUnreadMessageByUserProfileId(ObjectId id, Long userProfileId, int receiveInApp) {
        return super.countUnreadMessageByUserProfileId(id, userProfileId, receiveInApp);
    }

    @Override
    public List<ObjectIdCount> countUnreadMessageGroupByConversation(ObjectId messageCenterId, Long userProfileId, int receiveInApp) {
        return super.countUnreadMessageGroupByConversation(messageCenterId, userProfileId, receiveInApp);
    }

    @Override
    public List<ObjectIdCount> countUnreadMessageGroupByKey(String key, long userProfileId, int receiveInApp) {
        return super.countUnreadMessageGroupByKey(key, userProfileId, receiveInApp);
    }

    @Override
    public List<ObjectIdCount> countUnreadMessageGroupByMessageCenter(List<ObjectId> conversationIds, Long userProfileId, int receiveInApp) {
        return super.countUnreadMessageGroupByKey(MessageBase.Fields.messageCenterId, conversationIds, userProfileId, receiveInApp);
    }

    @Override
    public List<ObjectLatest> getLatestUpdatedDateByMessageCenterIds(List<ObjectId> messageCenterIds, Long userProfileId, int receiveInApp) {
        return super.getLatestUpdatedDateByKeyId(MessageBase.Fields.messageCenterId, messageCenterIds, userProfileId, receiveInApp);
    }

    @Override
    public List<ObjectLatest> getLatestUpdatedDateByConversationIds(List<ObjectId> conversationIds, Long userProfileId, int receiveInApp) {
        return super.getLatestUpdatedDateByKeyId(MessageBase.Fields.conversationId, conversationIds, userProfileId, receiveInApp);
    }

    @Override
    public long countUserSendUnreadMessageByUserProfileId(Long userProfileId, int receiveInApp) {
        return super.countUserSendUnreadMessageByUserProfileId(userProfileId, receiveInApp);
    }

    @Override
    public boolean addAESSecretKeyForNoneSecretKeyMessageByConversationIds(List<ConversationDoc> conversations) {
        return super.addAESSecretKeyForNoneSecretKeyMessageByConversationIds(conversations);
    }

    @Override
    protected Criteria initTypeUnreadCriteria() {
        return new Criteria().orOperator(initUnreadCriteria().toArray(new Criteria[0])) ;
    }

    @Override
    public List<SupportMessageDoc> getSentMessagesByUser(long userId, List<Integer> receiveInApps) {
        if (CollectionUtils.isNotEmpty(receiveInApps)) {
            Criteria receiveInAppCriteria = Criteria.where(MessageBase.Fields.receiveInApp).in(receiveInApps);
            Criteria sentMessages = new Criteria().andOperator(
                    Criteria.where(MessageBase.Fields.status).is(MessageConstants.MESSAGE_STATUS_SENT),
                    Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).is(userId),
                    recipientIsNotDeletedMessageCriteria, receiveInAppCriteria);

            return mongoTemplate.find(new Query(sentMessages), SupportMessageDoc.class);
        }
        return ImmutableList.of();
    }

    private void updateStatusForOlderActionMessage(SupportMessageDoc message, List<Integer> statusCondition, int statusUpdate) {
        if (isNotResponseMessageType(message.getType()) && Objects.nonNull(message.getRecipient())) {
            var recipientId = message.getRecipient().getUserProfileId();
            var criteriaNormalUpdate = initCriteriaUpdateStatusForReadOnlyMessage(message.getConversationId(), recipientId, message.getCreatedDate());
            var criteria = new Criteria().andOperator(Criteria.where(MessageBase.Fields.status).in(statusCondition), criteriaNormalUpdate);
            var update = new Update().set(MessageDocEnum.STATUS.getKey(), statusUpdate);
            mongoTemplate.updateMulti(new Query().addCriteria(criteria), update, SupportMessageDoc.class);
        }
    }

    @Override
    protected Criteria countUnreadMessage(long userProfileId, int receivedInApp) {
        var unreadCriteria = super.initUnreadCriteria();
        return new Criteria().andOperator(
                Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).is(userProfileId),
                recipientIsNotDeletedMessageCriteria,
                Criteria.where(MessageBase.Fields.receiveInApp).is(receivedInApp),
                new Criteria().orOperator(unreadCriteria.toArray(new Criteria[0]))
        );
    }
}
