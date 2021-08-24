package com.qooco.boost.data.mongo.services.impl.abstracts;

import com.google.common.collect.ImmutableList;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.enumeration.doc.MessageDocEnum;
import com.qooco.boost.data.model.ObjectLatest;
import com.qooco.boost.data.model.count.LongCount;
import com.qooco.boost.data.model.count.ObjectIdCount;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.qooco.boost.data.mongo.services.impl.Boot2MongoUtils.wasAcknowledged;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

public abstract class MessageDocAbstract<T extends MessageBase> {
    @Autowired
    protected MongoTemplate mongoTemplate;

    protected abstract Criteria countUnreadMessage(long userProfileId, int receivedInApp);

    protected abstract Criteria initTypeUnreadCriteria();

    private Class<T> getGenericType() {
        return (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), MessageDocAbstract.class);
    }

    protected static final Criteria senderIsNotDeletedMessageCriteria = new Criteria().orOperator(
            Criteria.where(MessageDocEnum.SENDER_IS_DELETED_MESSAGE.getKey()).exists(false),
            Criteria.where(MessageDocEnum.SENDER_IS_DELETED_MESSAGE.getKey()).is(false));

    protected static final Criteria recipientIsNotDeletedMessageCriteria = new Criteria().orOperator(
            Criteria.where(MessageDocEnum.RECIPIENT_IS_DELETED_MESSAGE.getKey()).exists(false),
            Criteria.where(MessageDocEnum.RECIPIENT_IS_DELETED_MESSAGE.getKey()).is(false));

    protected static final List<Integer> UNREAD_STATUS = ImmutableList.of(MessageConstants.MESSAGE_STATUS_SENT, MessageConstants.MESSAGE_STATUS_RECEIVED);

    protected List<T> findByConversationIdAndSizeAndTimestamp(ObjectId conversationId, Date timestamp, int size) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, MessageDocEnum.CREATED_DATE.getKey()));
        Criteria criteria = initFindByConversationIdAndSizeAndTimestampCriteria(conversationId, timestamp);
        return mongoTemplate.find(new Query(criteria).limit(size).with(sort), getGenericType());
    }

    protected long countByConversationIdAndSizeAndTimestamp(ObjectId conversationId, Date timestamp) {
        Criteria criteria = initFindByConversationIdAndSizeAndTimestampCriteria(conversationId, timestamp);
        return mongoTemplate.count(new Query(criteria), getGenericType());
    }

    protected List<ObjectIdCount> countUnreadMessageGroupByConversation(ObjectId messageCenterId, Long userProfileId, int receiveInApp) {
        var typeCriteria = countUnreadMessage(userProfileId, receiveInApp);
        var criteria = new Criteria().andOperator(
                Criteria.where(MessageDocEnum.MESSAGE_CENTER_ID.getKey()).is(messageCenterId),
                MessageDocEnum.IS_NOT_DELETED_CRITERIA, typeCriteria);

        return countMessageGroupByKey(criteria, MessageDocEnum.CONVERSATION_ID.getKey(), ObjectIdCount.class);
    }

    protected long countUnreadMessageByUserProfileId(ObjectId conversationId, Long userProfileId, int receiveInApp) {
        var unreadCriteria = countUnreadMessage(userProfileId, receiveInApp);
        var criteria = new Criteria().andOperator(Criteria.where(MessageBase.Fields.conversationId).is(conversationId),
                MessageDocEnum.IS_NOT_DELETED_CRITERIA, unreadCriteria);
        return mongoTemplate.count(new Query(criteria), getGenericType());
    }

    protected List<ObjectIdCount> countUnreadMessageGroupByKey(String key, long userProfileId, int receiveInApp) {
        Criteria typeCriteria = countUnreadMessage(userProfileId, receiveInApp);
        Criteria criteria = new Criteria().andOperator(MessageDocEnum.IS_NOT_DELETED_CRITERIA, typeCriteria);
        return countMessageGroupByKey(criteria, key, ObjectIdCount.class);
    }

    protected List<ObjectIdCount> countUnreadMessageGroupByKey(String key, List<ObjectId> conversationIds, Long userProfileId, int receiveInApp) {
        if (CollectionUtils.isNotEmpty(conversationIds)) {
            Criteria typeCriteria = countUnreadMessage(userProfileId, receiveInApp);
            Criteria criteria = new Criteria().andOperator(
                    Criteria.where(MessageBase.Fields.conversationId).in(conversationIds),
                    MessageDocEnum.IS_NOT_DELETED_CRITERIA, typeCriteria);
            return countMessageGroupByKey(criteria, key, ObjectIdCount.class);
        }
        return ImmutableList.of();
    }

    protected long countUserSendUnreadMessageByUserProfileId(Long userProfileId, int receiveInApp) {
        var criteria = countUnreadMessage(userProfileId, receiveInApp);
        return mongoTemplate.count(new Query(criteria), getGenericType());
    }

    protected List<ObjectLatest> getLatestUpdatedDateByKeyId(String key, List<ObjectId> ids, Long userProfileId, int receiveInApp) {
        if (CollectionUtils.isNotEmpty(ids)) {
            var messageCenterCriteria = Criteria.where(key).in(key);
            return getLatestUpdatedDateByKey(key, userProfileId, messageCenterCriteria, receiveInApp);
        }
        return ImmutableList.of();
    }

    public boolean addAESSecretKeyForNoneSecretKeyMessageByConversationIds(List<ConversationDoc> conversationDocs) {
        if (CollectionUtils.isNotEmpty(conversationDocs)) {
            var bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, getGenericType());
            conversationDocs.forEach(conversationDoc -> {
                var criteria = new Criteria().andOperator(
                        Criteria.where(MessageBase.Fields.conversationId).is(conversationDoc.getId()),
                        Criteria.where(MessageBase.Fields.secretKey).exists(false));
                var updater = new Update().set(MessageDocEnum.SECRET_KEY.getKey(), conversationDoc.getSecretKey());
                bulkOps.updateOne(new Query(criteria), updater);
            });
            return wasAcknowledged(bulkOps.execute());
        }
        return false;
    }

    private List<ObjectLatest> getLatestUpdatedDateByKey(String groupField, Long userProfileId, Criteria criteriaAnd, int receiveInApp) {
        var senderCriteria = Criteria.where(MessageDocEnum.SENDER_USER_PROFILE_ID.getKey()).ne(userProfileId);
        var receiveInAppCriteria = Criteria.where(MessageBase.Fields.receiveInApp).is(receiveInApp);
        var unreadMessageCriteria = initTypeUnreadCriteria();

        var criteria = new Criteria().andOperator(
                criteriaAnd,
                MessageDocEnum.IS_NOT_DELETED_CRITERIA,
                senderCriteria,
                senderIsNotDeletedMessageCriteria,
                receiveInAppCriteria,
                unreadMessageCriteria);

        var aggregation = newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(groupField).last(MessageBase.Fields.updatedDate).as(ObjectLatest.Fields.lastUpdateTime));

        return mongoTemplate.aggregate(aggregation, getGenericType(), ObjectLatest.class).getMappedResults();
    }

    private <E> List<E> countMessageGroupByKey(Criteria criteria, String keyId, Class<E> outputType) {
        Aggregation aggregation = newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(keyId).count().as(LongCount.Fields.total),
                Aggregation.sort(Sort.Direction.DESC, LongCount.Fields.total));

        AggregationResults<E> groupResults = mongoTemplate.aggregate(aggregation, getGenericType(), outputType);
        return groupResults.getMappedResults();
    }

    protected List<Criteria> initUnreadCriteria() {
        Criteria statusCriteria = new Criteria().orOperator(
                Criteria.where(MessageBase.Fields.status).in(MessageConstants.MESSAGE_STATUS_SENT),
                Criteria.where(MessageBase.Fields.status).in(MessageConstants.MESSAGE_STATUS_RECEIVED));

        Criteria textMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.TEXT_MESSAGE),
                statusCriteria);

        Criteria mediaMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.MEDIA_MESSAGE),
                statusCriteria);

        return Arrays.asList(textMessage, mediaMessage);
    }

    private Criteria initFindByConversationIdAndSizeAndTimestampCriteria(ObjectId conversationId, Date timestamp) {
        Criteria createdDateCriteria = Criteria.where(MessageBase.Fields.createdDate).lt(timestamp);
        return new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.conversationId).is(conversationId),
                MessageDocEnum.IS_NOT_DELETED_CRITERIA, createdDateCriteria);
    }

    protected Criteria initCriteriaUpdateStatusForReadOnlyMessage(ObjectId conversationId, long recipientUserId, Date createdDate) {
        return new Criteria().andOperator(
                Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).is(recipientUserId),
                Criteria.where(MessageBase.Fields.conversationId).is(conversationId),
                Criteria.where(MessageBase.Fields.createdDate).lt(createdDate),
                recipientIsNotDeletedMessageCriteria,
                MessageDocEnum.IS_NOT_DELETED_CRITERIA
        );
    }

}
