package com.qooco.boost.data.mongo.services.impl.abstracts;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.constants.ConversationDocConstants;
import com.qooco.boost.data.enumeration.embedded.UserProfileCvEmbeddedEnum;
import com.qooco.boost.data.enumeration.embedded.UserProfileEmbeddedEnum;
import com.qooco.boost.data.mongo.embedded.PublicKeyEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileBasicEmbedded;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.oracle.services.UserAccessTokenService;
import com.qooco.boost.data.oracle.services.UserFitService;
import com.qooco.boost.data.utils.CipherKeys;
import com.qooco.boost.models.sdo.ConversationPublicKeySDO;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.crypto.SecretKey;
import java.util.*;

import static com.qooco.boost.data.mongo.services.impl.Boot2MongoUtils.getModifiedCount;
import static com.qooco.boost.data.mongo.services.impl.Boot2MongoUtils.wasAcknowledged;
import static java.util.Optional.ofNullable;

public abstract class ConversationDocAbstract<T extends ConversationBase> {
    @Autowired
    private UserAccessTokenService userAccessTokenService;
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    protected UserCvDocService userCvDocService;
    @Autowired
    protected UserFitService userFitService;

    private Class<T> getGenericType() {
     return   (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), ConversationDocAbstract.class);
    }
    @NotNull
    protected String generateSecretKey() {
        SecretKey secretKey = CipherKeys.generateAESKey();
        return CipherKeys.convertToBase64String(secretKey);
    }

    protected Map<String, PublicKeyEmbedded> createEncryptUserKey(List<Long> userProfileIds, String secretKey) {
        var userAccessTokens = userAccessTokenService.findByUserProfileIdsAndValidPublicKey(userProfileIds);
        var userKeyMap = new HashMap<String, PublicKeyEmbedded>();
        userAccessTokens.forEach(u -> {
            String token = u.getToken();
            PublicKeyEmbedded publicKeyEmbedded = new PublicKeyEmbedded(u.getPublicKey(), secretKey);
            userKeyMap.put(token, publicKeyEmbedded);
        });
        return userKeyMap;
    }

    protected long countByMessageCenterIdAndUserProfileIdWithTimestamp(ObjectId messageCenterId, Long userProfileId, Long timestamp) {
        var query = initQueryFindByMessageCenterIdAndUserProfileIdWithTimestamp(ImmutableList.of(messageCenterId), userProfileId, timestamp, 1);
        return mongoTemplate.count(query, getGenericType());
    }

    protected List<T> findByMessageCenterIdAndUserProfileIdWithTimestamp(ObjectId messageCenterId, Long userProfileId, Long timestamp, int size) {
        var query = initQueryFindByMessageCenterIdAndUserProfileIdWithTimestamp(ImmutableList.of(messageCenterId), userProfileId, timestamp, size);
        return mongoTemplate.find(query, getGenericType());
    }

    protected T findByIdAndUserProfileId(ObjectId id, Long userProfileId) {
        var userCriteria = new Criteria().andOperator(
                Criteria.where(UserProfileBasicEmbedded.Fields.userProfileId).is(userProfileId),
                UserProfileCvEmbeddedEnum.IS_NOT_DELETED_MESSAGE_CRITERIA);

        var participantsCriteria = new Criteria(ConversationBase.Fields.participants).elemMatch(userCriteria);

        var criteria = new Criteria().andOperator(
                participantsCriteria,
                Criteria.where(ConversationBase.Fields.id).is(id),
                Criteria.where(ConversationBase.Fields.isDeleted).is(false));
        return mongoTemplate.findOne(new Query().addCriteria(criteria), getGenericType());
    }

    protected void updateNowForUpdatedDate(List<ObjectId> ids, Date date) {
        if (CollectionUtils.isNotEmpty(ids) && Objects.nonNull(date)) {
            var criteria = Criteria.where(ConversationDocConstants.ID).in(ids);
            var update = new Update().set(ConversationDocConstants.UPDATED_DATE, date);
            mongoTemplate.updateMulti(new Query(criteria), update, getGenericType());
        }
    }

    protected List<T> findByMessageCenterIdAndUserProfileId(List<ObjectId> messageCenterIds, Long userProfile, List<String> includeFields) {
        Query query = initQueryFindByMessageCenterIdAndUserProfileId(messageCenterIds, userProfile);
        ofNullable(includeFields).filter(CollectionUtils::isNotEmpty).ifPresent(it -> it.forEach(query.fields()::include));
        return mongoTemplate.find(query, getGenericType());
    }

    protected Query initQueryFindByMessageCenterIdAndUserProfileId(List<ObjectId> messageCenterIds, Long userProfile) {
        Criteria criteria = initCriteriaFindByMessageCenterIdAndUserProfileId(messageCenterIds, Lists.newArrayList(userProfile));
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, ConversationDocConstants.UPDATED_DATE));
        return new Query(criteria).with(sort);
    }

    protected boolean updateBatchSecretKeyForConversations(List<ConversationDoc> conversationDocs) {
        if (CollectionUtils.isNotEmpty(conversationDocs)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, getGenericType());
            conversationDocs.forEach(conversationDoc -> {
                Criteria criteria = Criteria.where(ConversationDocConstants.ID).is(conversationDoc.getId());
                Update updater = new Update().set(ConversationDocConstants.SECRET_KEY, conversationDoc.getSecretKey());
                bulkOps.updateOne(new Query(criteria), updater);
            });
            return wasAcknowledged(bulkOps.execute());
        }
        return false;
    }

    protected boolean updateBatchEncryptionKeyForConversationDocs(List<ConversationPublicKeySDO> conversationPublicKeySDOS) {
        if (CollectionUtils.isNotEmpty(conversationPublicKeySDOS)) {
            var bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, getGenericType());
            conversationPublicKeySDOS.forEach(conversationPublicKey -> {
                var criteria = Criteria.where(ConversationDocConstants.ID).is(conversationPublicKey.getConversationId());
                var accessTokenBuilder = new StringBuilder(ConversationDocConstants.ENCRYPTED_PUBLIC_KEYS)
                        .append(Constants.DOT)
                        .append(conversationPublicKey.getAccessToken());
                var updater = new Update().set(accessTokenBuilder.toString(), mongoTemplate.getConverter().convertToMongoType(conversationPublicKey.getPublicKey()));

                bulkOps.updateMulti(new Query(criteria), updater);
            });
            return wasAcknowledged(bulkOps.execute());
        }
        return false;
    }

    protected int removeAccessTokenInUserKey(String accessToken) {
        var accessTokenBuilder = new StringBuilder(ConversationDocConstants.ENCRYPTED_PUBLIC_KEYS).append(Constants.DOT).append(accessToken);
        var keyCriteria = Criteria.where(accessTokenBuilder.toString()).exists(true);
        Update updater = new Update().unset(accessTokenBuilder.toString());
        return (int) getModifiedCount(mongoTemplate.updateMulti(new Query(keyCriteria), updater, getGenericType()));
    }

    protected List<T> findNotHavePublicKeyByToken(Long userId, String accessToken, String publicKey, int limit) {
        if (StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(publicKey)) {
            Criteria keyCriteria;
            StringBuilder accessTokenBuilder = new StringBuilder(ConversationDocConstants.ENCRYPTED_PUBLIC_KEYS);
            accessTokenBuilder.append(Constants.DOT).append(accessToken);
            String accessTokenQuery = accessTokenBuilder.toString();
            if (StringUtils.isNotBlank(publicKey)) {
                String publicKeyCriteria = accessTokenBuilder.append(Constants.DOT).
                        append(ConversationDocConstants.PUBLIC_KEY).toString();
                keyCriteria = new Criteria().orOperator(
                        Criteria.where(accessTokenQuery).exists(false),
                        Criteria.where(publicKeyCriteria).nin(publicKey));
            } else {
                keyCriteria = Criteria.where(accessTokenQuery).exists(false);
            }
            Criteria criteria = new Criteria().andOperator(keyCriteria,
                    Criteria.where(ConversationDocConstants.PARTICIPANTS_USER_PROFILE_ID).is(userId));
            return mongoTemplate.find(new Query(criteria).limit(limit), getGenericType());
        }
        return new ArrayList<>();
    }

    private Query initQueryFindByMessageCenterIdAndUserProfileIdWithTimestamp(List<ObjectId> messageCenterIds, Long userProfile, Long timestamp, int size) {
        var criteria = initCriteriaFindByMessageCenterIdAndUserProfileId(messageCenterIds, Lists.newArrayList(userProfile));
        Query query;
        if (Objects.nonNull(timestamp)) {
            var timestampCriteria = Criteria.where(ConversationDocConstants.UPDATED_DATE).lt(new Date(timestamp));
            var queryCriteria = new Criteria();
            queryCriteria.andOperator(criteria, timestampCriteria);
            query = new Query(queryCriteria);
        } else {
            query = new Query(criteria);
        }
        var sort = Sort.by(new Sort.Order(Sort.Direction.DESC, ConversationDocConstants.UPDATED_DATE));
        return query.with(sort).limit(size);
    }

    private Criteria initCriteriaFindByMessageCenterIdAndUserProfileId(List<ObjectId> messageCenterIds, List<Long> userProfiles) {
        var userCriteria = new Criteria().andOperator(
                Criteria.where(UserProfileEmbeddedEnum.USER_PROFILE_ID.getKey()).in(userProfiles),
                UserProfileCvEmbeddedEnum.IS_NOT_DELETED_MESSAGE_CRITERIA);

        return new Criteria().andOperator(
                new Criteria(ConversationDocConstants.PARTICIPANTS).elemMatch(userCriteria),
                Criteria.where(ConversationDocConstants.MESSAGE_CENTER_ID).in(messageCenterIds),
                Criteria.where(ConversationDocConstants.IS_DELETED).is(false));
    }
}
