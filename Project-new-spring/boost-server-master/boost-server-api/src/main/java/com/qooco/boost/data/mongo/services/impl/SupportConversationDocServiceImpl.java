package com.qooco.boost.data.mongo.services.impl;

import com.google.common.collect.ImmutableList;
import com.qooco.boost.data.constants.ConversationDocConstants;
import com.qooco.boost.data.constants.SupportChannelStatus;
import com.qooco.boost.data.mongo.embedded.UserProfileBasicEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.entities.SupportConversationDoc;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import com.qooco.boost.data.mongo.repositories.SupportConversationDocRepository;
import com.qooco.boost.data.mongo.services.SupportConversationDocService;
import com.qooco.boost.data.mongo.services.impl.abstracts.ConversationDocAbstract;
import com.qooco.boost.models.sdo.ConversationPublicKeySDO;
import com.qooco.boost.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.repository.support.PageableExecutionUtils.getPage;

@Service
@RequiredArgsConstructor
public class SupportConversationDocServiceImpl extends ConversationDocAbstract<SupportConversationDoc> implements SupportConversationDocService {
    protected Logger logger = LogManager.getLogger(SupportConversationDocServiceImpl.class);

    private final SupportConversationDocRepository repository;

    @Override
    public MongoRepository getRepository() {
        return repository;
    }

    @Override
    public SupportConversationDoc save(MessageCenterDoc messageCenter, UserProfileCvEmbedded sender, UserProfileCvEmbedded recipient, int fromApp) {
        var channelSupport = findOneBySenderAndFromAppStatus(sender.getUserProfileId(), sender.getUserType(), fromApp, SupportChannelStatus.OPENING);
        return save(messageCenter, channelSupport, sender, recipient, fromApp);
    }

    @Override
    public SupportConversationDoc findBySenderAndFromApp(long userProfileId, int userType, int fromApp) {
        return findOneBySenderAndFromAppStatus(userProfileId, userType, fromApp, SupportChannelStatus.OPENING);
    }

    @Override
    public long countByMessageCenterIdAndUserProfileIdWithTimestamp(ObjectId messageCenterId, Long userProfileId, Long timestamp) {
        return super.countByMessageCenterIdAndUserProfileIdWithTimestamp(messageCenterId, userProfileId, timestamp);
    }

    @Override
    public List<SupportConversationDoc> findByMessageCenterIdAndUserProfileIdWithTimestamp(ObjectId messageCenterId, Long userProfileId, Long timestamp, int size) {
        return super.findByMessageCenterIdAndUserProfileIdWithTimestamp(messageCenterId, userProfileId, timestamp, size);
    }

    @Override
    public SupportConversationDoc findByIdAndUserProfileId(ObjectId id, Long userProfileId) {
        return super.findByIdAndUserProfileId(id, userProfileId);
    }

    @Override
    public void updateNowForUpdatedDate(List<ObjectId> ids, Date date) {
        super.updateNowForUpdatedDate(ids, date);
    }

    @Override
    public List<SupportConversationDoc> findByMessageCenterIdAndUserProfileId(List<ObjectId> messageCenterIds, Long userProfile, List<String> includeFields) {
        return super.findByMessageCenterIdAndUserProfileId(messageCenterIds, userProfile, includeFields);
    }

    @Override
    public boolean updateBatchSecretKeyForConversations(List<ConversationDoc> conversationDocs) {
        return super.updateBatchSecretKeyForConversations(conversationDocs);
    }

    @Override
    public boolean updateBatchEncryptionKeyForConversationDocs(List<ConversationPublicKeySDO> conversationPublicKeySDOS) {
        return super.updateBatchEncryptionKeyForConversationDocs(conversationPublicKeySDOS);
    }

    @Override
    public int removeAccessTokenInUserKey(String accessToken) {
        return super.removeAccessTokenInUserKey(accessToken);
    }

    @Override
    public List<SupportConversationDoc> findNotHavePublicKeyByToken(Long userId, String accessToken, String publicKey, int limit) {
        return super.findNotHavePublicKeyByToken(userId, accessToken, publicKey, limit);
    }

    private SupportConversationDoc findOneBySenderAndFromAppStatus(String messageCenterId) {
        if (ObjectId.isValid(messageCenterId)) {
            var criteria = where(ConversationBase.Fields.messageCenterId).is(messageCenterId);
            var sort = Sort.by(new Order(DESC, ConversationDocConstants.UPDATED_DATE));
            return mongoTemplate.findOne(new Query().addCriteria(criteria).with(sort), SupportConversationDoc.class);
        }
        return null;
    }

    @Override
    public void updateStatus(String id, int status) {
        var criteria = new Criteria().andOperator(
                where(ConversationBase.Fields.id).is(id),
                where(ConversationBase.Fields.isDeleted).is(false),
                where(SupportConversationDoc.Fields.status).nin(status)
        );
        var updater = new Update()
                .set(SupportConversationDoc.Fields.status, status)
                .set(ConversationBase.Fields.updatedDate, DateUtils.toServerTimeForMongo());
        mongoTemplate.updateMulti(new Query().addCriteria(criteria), updater, SupportConversationDoc.class);
    }

    public Page<SupportConversationDoc> findByAppAndStatusAndLanguageSortBy(Integer appId, Integer status, int page, int size, String... sortBy) {
        var criteria = new Criteria().andOperator(Stream.of(
                where(ConversationBase.Fields.isDeleted).is(false),
                ofNullable(appId).map(it -> where(SupportConversationDoc.Fields.fromApp).is(it)).orElse(null),
                ofNullable(status).map(it -> where(SupportConversationDoc.Fields.status).is(it)).orElse(null)
        ).filter(Objects::nonNull).toArray(Criteria[]::new));

        var sort = Sort.by(stream(sortBy)
                .map(it -> List.of(it.split(" ")))
                .map(it -> new Order(it.stream().skip(it.size() - 1).findFirst().filter(by -> by.equalsIgnoreCase("desc")).map(e -> DESC).orElse(ASC), it.stream().findFirst().orElse("")))
                .collect(toUnmodifiableList()));
        var pageable = PageRequest.of(page, size);
        var query = new Query().addCriteria(criteria).with(sort).with(pageable);
        return getPage(mongoTemplate.find(query, SupportConversationDoc.class), pageable, () -> mongoTemplate.count(query, SupportConversationDoc.class));
    }

    private SupportConversationDoc findOneBySenderAndFromAppStatus(long userProfileId, int userType, int fromApp, int status) {
        var criteriaParticipant = new Criteria().andOperator(
                where(UserProfileBasicEmbedded.Fields.userProfileId).is(userProfileId),
                where(UserProfileBasicEmbedded.Fields.userType).is(userType)
        );

        Criteria elementMatching = where(ConversationBase.Fields.participants).elemMatch(criteriaParticipant);
        var criteria = new Criteria().andOperator(
                elementMatching,
                where(ConversationBase.Fields.isDeleted).is(false),
                where(SupportConversationDoc.Fields.status).is(status),
                where(SupportConversationDoc.Fields.fromApp).is(fromApp));
        var sort = Sort.by(new Order(DESC, ConversationDocConstants.UPDATED_DATE));

        return mongoTemplate.findOne(new Query().addCriteria(criteria).with(sort), SupportConversationDoc.class);
    }

    private SupportConversationDoc save(MessageCenterDoc messageCenter, SupportConversationDoc doc, UserProfileCvEmbedded sender, UserProfileCvEmbedded recipient, int fromApp) {
        if (Objects.isNull(doc)) {

            var conversationDoc = new SupportConversationDoc(messageCenter, sender, recipient, fromApp);
            conversationDoc.setCreatedBy(sender);
            // Save conversation if not can not find the conversation in findByMessageCenterIdAndSenderAndRecipient
            doc = repository.save(conversationDoc);
        }

        if (Objects.isNull(doc.getSecretKey())) {
            doc.setSecretKey(generateSecretKey());
            var userKeyMap = createEncryptUserKey(ImmutableList.of(sender.getUserProfileId()), doc.getSecretKey());
            doc.setUserKeys(userKeyMap);
        }

        doc.setUpdatedDate(DateUtils.toServerTimeForMongo());
        return repository.save(doc);
    }
}
