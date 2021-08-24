package com.qooco.boost.data.mongo.services.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.constants.ConversationDocConstants;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.enumeration.ContactPersonStatus;
import com.qooco.boost.data.enumeration.doc.MessageCenterDocEnum;
import com.qooco.boost.data.enumeration.embedded.CompanyEmbeddedEnum;
import com.qooco.boost.data.enumeration.embedded.UserProfileCvEmbeddedEnum;
import com.qooco.boost.data.enumeration.embedded.UserProfileEmbeddedEnum;
import com.qooco.boost.data.enumeration.embedded.VacancyEmbeddedEnum;
import com.qooco.boost.data.model.ObjectIdList;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.repositories.ConversationDocRepository;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.impl.abstracts.ConversationDocAbstract;
import com.qooco.boost.models.sdo.ConversationPublicKeySDO;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
public class ConversationDocServiceImpl extends ConversationDocAbstract<ConversationDoc> implements ConversationDocService {
    protected Logger logger = LogManager.getLogger(ConversationDocServiceImpl.class);
    @Autowired
    private ConversationDocRepository repository;

    private static final List<CompanyRole> recruiterRoles = ImmutableList.of(CompanyRole.ADMIN, CompanyRole.HEAD_RECRUITER, CompanyRole.RECRUITER);
    private static final List<CompanyRole> normalRoles = ImmutableList.of(CompanyRole.ANALYST, CompanyRole.NORMAL_USER);

    @Override
    public MongoRepository<ConversationDoc, ObjectId> getRepository() {
        return repository;
    }

    @Override
    public List<ConversationDoc> findByLimit(List<ObjectId> excludeIds, int limit) {
        var criteria = Criteria.where(ConversationDocConstants.ID).nin(excludeIds);
        return mongoTemplate.find(new Query(criteria).limit(limit), ConversationDoc.class);
    }

    @Override
    public List<ConversationDoc> findByMessageCenterId(ObjectId messageCenterId) {
        var criteria = new Criteria().andOperator(
                Criteria.where(ConversationDocConstants.MESSAGE_CENTER_ID).is(messageCenterId),
                Criteria.where(ConversationDocConstants.IS_DELETED).is(false));

        return mongoTemplate.find(new Query(criteria), ConversationDoc.class);
    }

    @Override
    public List<ConversationDoc> findByMessageCenterIdAndUserProfileId(ObjectId messageCenterId, Long userProfileId) {
        return findByMessageCenterIdAndUserProfileId(Lists.newArrayList(messageCenterId), userProfileId);
    }

    @Override
    public List<ConversationDoc> findByMessageCenterAndParticipant(ObjectId messageCenterId, List<List<Long>> userProfileGroup) {
        if (CollectionUtils.isNotEmpty(userProfileGroup)) {
            var criteriaParticipants = new ArrayList<Criteria>();
            userProfileGroup.forEach(userProfileIds -> {
                criteriaParticipants.add(new Criteria().andOperator(
                        Criteria.where(ConversationDocConstants.MESSAGE_CENTER_ID).is(messageCenterId),
                        Criteria.where(ConversationDocConstants.PARTICIPANTS_USER_PROFILE_ID).all(userProfileIds),
                        Criteria.where(ConversationDocConstants.IS_DELETED).is(false)
                ));
            });

            var criteria = new Criteria().orOperator(criteriaParticipants.toArray(new Criteria[0]));
            return mongoTemplate.find(new Query(criteria), ConversationDoc.class);
        }
        return new ArrayList<>();
    }

    @Override
    public List<ConversationDoc> findByMessageCenterIdAndUserProfileId(List<ObjectId> messageCenterIds, Long userProfile) {
        Query query = initQueryFindByMessageCenterIdAndUserProfileId(messageCenterIds, userProfile);
        return mongoTemplate.find(query, ConversationDoc.class);
    }

    @Override
    public void setDisableConversations(Long companyId, Long userProfileId, CompanyRole newRole, CompanyRole oldRole) {
        if ((recruiterRoles.contains(newRole) && normalRoles.contains(oldRole))
                || recruiterRoles.contains(oldRole) && normalRoles.contains(newRole)) {

            String companyOfVacancyKey = StringUtil.append(
                    MessageCenterDocEnum.VACANCY.getKey(), Constants.DOT,
                    VacancyEmbeddedEnum.COMPANY.getKey(), Constants.DOT,
                    CompanyEmbeddedEnum.ID.getKey()
            );
            Criteria companyCriteria = Criteria.where(companyOfVacancyKey).is(companyId);

            String contactPersonKey = StringUtil.append(MessageCenterDocEnum.CONTACT_PERSONS.getKey(), Constants.DOT, UserProfileEmbeddedEnum.USER_PROFILE_ID.getKey());
            String managerKey = StringUtil.append(MessageCenterDocEnum.APPOINTMENT_MANAGERS.getKey(), Constants.DOT, UserProfileEmbeddedEnum.USER_PROFILE_ID.getKey());

            Criteria staffCriteria = new Criteria().orOperator(
                    Criteria.where(contactPersonKey).is(userProfileId),
                    Criteria.where(managerKey).is(userProfileId));
            Criteria criteria = new Criteria().andOperator(companyCriteria, staffCriteria);
            Query query = new Query().addCriteria(criteria);
            List<MessageCenterDoc> results = mongoTemplate.find(query, MessageCenterDoc.class);
            if (CollectionUtils.isNotEmpty(results)) {
                List<ObjectId> messageCenterIds = results.stream().map(MessageCenterDoc::getId).collect(Collectors.toList());
                boolean isDisable = !(CompanyRole.ADMIN.equals(newRole) || CompanyRole.HEAD_RECRUITER.equals(newRole) || CompanyRole.RECRUITER.equals(newRole));
                setDisableConversations(messageCenterIds, userProfileId, isDisable);
            }
        }
    }

    @Override
    public void setChangedContactPersonStatus(Long companyId, Long oldUserId, CompanyRole newRole, CompanyRole oldRole) {
        if (recruiterRoles.contains(oldRole) && normalRoles.contains(newRole)) {
            Criteria criteria = new Criteria().andOperator(
                    Criteria.where(ConversationDocConstants.COMPANY_ID).is(companyId),
                    Criteria.where(ConversationDocConstants.PARTICIPANTS_USER_PROFILE_ID).is(oldUserId),
                    Criteria.where(ConversationDocConstants.CONTACT_PERSON_STATUS).nin(ContactPersonStatus.CHANGED.getCode()),
                    Criteria.where(ConversationDocConstants.IS_DELETED).is(false));
            Update updaterForOldContact = new Update().set(ConversationDocConstants.CONTACT_PERSON_STATUS, ContactPersonStatus.CHANGED.getCode());
            mongoTemplate.updateMulti(new Query(criteria), updaterForOldContact, ConversationDoc.class);
        }
    }

    @Override
    public void setChangedContactPersonStatus(Long id, ContactPersonStatus contactPersonStatus) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(ConversationDocConstants.ID).is(id),
                Criteria.where(ConversationDocConstants.IS_DELETED).is(false));
        Update updaterForOldContact = new Update().set(ConversationDocConstants.CONTACT_PERSON_STATUS, contactPersonStatus.getCode());
        mongoTemplate.updateMulti(new Query(criteria), updaterForOldContact, ConversationDoc.class);
    }

    @Override
    public void setDisableConversations(List<ObjectId> messageCenterIds, Long userProfileId, boolean isDisable) {
        if (CollectionUtils.isNotEmpty(messageCenterIds)) {
            Criteria criteria = new Criteria().andOperator(
                    Criteria.where(ConversationDocConstants.MESSAGE_CENTER_ID).in(messageCenterIds),
                    Criteria.where(ConversationDocConstants.PARTICIPANTS_USER_PROFILE_ID).is(userProfileId),
                    Criteria.where(ConversationDocConstants.IS_DELETED).is(false));

            Update update = new Update();
            update.set(ConversationDocConstants.IS_DISABLE, isDisable);
            mongoTemplate.updateMulti(new Query(criteria), update, ConversationDoc.class);
        }
    }

    @Override
    public void setDisableConversations(List<ObjectId> ids, boolean isDisable) {
        if (CollectionUtils.isNotEmpty(ids)) {
            Criteria criteria = new Criteria().andOperator(
                    Criteria.where(ConversationDocConstants.ID).in(ids),
                    Criteria.where(ConversationDocConstants.IS_DELETED).is(false));
            Update update = new Update();
            update.set(ConversationDocConstants.IS_DISABLE, isDisable);
            mongoTemplate.updateMulti(new Query(criteria), update, ConversationDoc.class);
        }
    }

    @Override
    public void deleteConversation(List<ObjectId> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            Criteria criteria = Criteria.where(ConversationDocConstants.ID).in(ids);
            Update update = new Update().set(ConversationDocConstants.IS_DELETED, true);
            mongoTemplate.updateMulti(new Query(criteria), update, ConversationDoc.class);
        }
    }

    @Override
    public void deleteConversation(ObjectId messageCenterId, Long userProfileId) {
        Criteria isDeletedCriteria = Criteria.where(ConversationDocConstants.IS_DELETED).is(false);
        Criteria messageCenterCriteria = Criteria.where(ConversationDocConstants.MESSAGE_CENTER_ID).is(messageCenterId);
        Criteria participantCriteria = Criteria.where(ConversationDocConstants.PARTICIPANTS_USER_PROFILE_ID).is(userProfileId);
        Criteria criteria = new Criteria().andOperator(messageCenterCriteria, participantCriteria, isDeletedCriteria);

        String participantsIsDeletedMessageKey = StringUtil.append(ConversationDocConstants.PARTICIPANTS, Constants.DOLLAR, UserProfileCvEmbeddedEnum.IS_DELETED_MESSAGE.getKey());
        Update update = new Update().set(participantsIsDeletedMessageKey, true);
        mongoTemplate.updateMulti(new Query(criteria), update, ConversationDoc.class);
    }

    @Override
    public ConversationDoc findByMessageCenterIdAndSenderAndRecipient(ObjectId messageCenterDocId, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv) {
        var sort = Sort.by(new Sort.Order(Sort.Direction.DESC, ConversationDocConstants.UPDATED_DATE));
        var criteria = new Criteria().andOperator(
                Criteria.where(ConversationDocConstants.MESSAGE_CENTER_ID).is(messageCenterDocId),
                Criteria.where(ConversationDocConstants.PARTICIPANTS_USER_PROFILE_ID).is(senderCv.getUserProfileId()),
                Criteria.where(ConversationDocConstants.PARTICIPANTS_USER_PROFILE_ID).is(recipientCv.getUserProfileId()),
                Criteria.where(ConversationDocConstants.IS_DELETED).is(false));

        return mongoTemplate.findOne(new Query().addCriteria(criteria).with(sort), ConversationDoc.class);
    }

    @Override
    public List<ObjectIdList> getConversationsGroupByMessageCenter(Long company, Long userProfile) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(ConversationDocConstants.COMPANY_ID).is(company),
                Criteria.where(ConversationDocConstants.PARTICIPANTS_USER_PROFILE_ID).is(userProfile),
                Criteria.where(ConversationDocConstants.IS_DELETED).is(false));
        return getConversationGroupByMessageCenter(criteria);
    }

    private List<ObjectIdList> getConversationGroupByMessageCenter(Criteria criteria) {
        Aggregation aggregation = newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(ConversationDocConstants.MESSAGE_CENTER_ID)
                        .addToSet(ConversationDocConstants.ID).as(ObjectIdList.Fields.result));

        AggregationResults<ObjectIdList> groupResults = mongoTemplate.aggregate(aggregation, ConversationDoc.class, ObjectIdList.class);
        return groupResults.getMappedResults();
    }

    @Override
    public ConversationDoc save(MessageCenterDoc messageCenterDoc, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv) {
        return save(messageCenterDoc, senderCv, recipientCv, ContactPersonStatus.NORMAL.getCode());
    }

    @Override
    public ConversationDoc save(MessageCenterDoc messageCenterDoc, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv, int contactPersonStatus) {
        var conversationDocInDb = findByMessageCenterIdAndSenderAndRecipient(messageCenterDoc.getId(), senderCv, recipientCv);

        if (Objects.isNull(conversationDocInDb)) {
            var participants = List.of(senderCv, recipientCv);
            var conversationDoc = new ConversationDoc(messageCenterDoc, participants);
            conversationDoc.setCreatedBy(senderCv);
            conversationDoc.setContactPersonStatus(contactPersonStatus);
            // Save conversation if not can not find the conversation in findByMessageCenterIdAndSenderAndRecipient
            conversationDocInDb = repository.save(conversationDoc);
        }

        if (Objects.isNull(conversationDocInDb.getSecretKey())) {
            conversationDocInDb.setSecretKey(generateSecretKey());
            var userKeyMap = createEncryptUserKey(ImmutableList.of(senderCv.getUserProfileId(), recipientCv.getUserProfileId()), conversationDocInDb.getSecretKey());
            conversationDocInDb.setUserKeys(userKeyMap);
        }

        conversationDocInDb.setUpdatedDate(DateUtils.toServerTimeForMongo());
        conversationDocInDb.setContactPersonStatus(contactPersonStatus);
        return repository.save(conversationDocInDb);
    }

    @Override
    public List<ConversationDoc> findNonSecretKeyConversation(Long userId) {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where(ConversationDocConstants.SECRET_KEY).exists(false),
                Criteria.where(ConversationDocConstants.PARTICIPANTS_USER_PROFILE_ID).is(userId));
        return mongoTemplate.find(new Query(criteria), ConversationDoc.class);
    }


    @Override
    public void updateParticipant(Map<ObjectId, List<UserProfileCvEmbedded>> participantMap) {
        if (MapUtils.isNotEmpty(participantMap)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ConversationDoc.class);
            participantMap.forEach((conversationId, participants) -> {
                Update updater = new Update();
                updater.set(ConversationDocConstants.PARTICIPANTS, participants);
                Criteria criteria = Criteria.where(ConversationDocConstants.ID).is(conversationId);
                Query query = new Query(criteria);
                bulkOps.updateOne(query, updater);
            });
            bulkOps.execute();
        }
    }

    @Override
    public void updateCreatedBy(Map<ObjectId, UserProfileCvEmbedded> createdByMap) {
        if (MapUtils.isNotEmpty(createdByMap)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ConversationDoc.class);
            createdByMap.forEach((conversationId, createdBy) -> {
                Update updater = new Update();
                updater.set(ConversationDocConstants.CREATED_BY, createdBy);
                Criteria criteria = Criteria.where(ConversationDocConstants.ID).is(conversationId);
                Query query = new Query(criteria);
                bulkOps.updateOne(query, updater);
            });
            bulkOps.execute();
        }
    }

    @Override
    public void syncIsDeletedMessageInConversationDoc() {
        Criteria isDeletedMessageKey = Criteria.where(UserProfileCvEmbeddedEnum.IS_DELETED_MESSAGE.getKey()).exists(false);
        Criteria userKey = Criteria.where(UserProfileEmbeddedEnum.USER_PROFILE_ID.getKey()).exists(true);
        Criteria participantsCriteria = new Criteria().andOperator(isDeletedMessageKey, userKey);
        Criteria queryKey = Criteria.where(ConversationDocConstants.PARTICIPANTS).elemMatch(participantsCriteria);

        String updateKey = StringUtil.append(ConversationDocConstants.PARTICIPANTS, Constants.DOLLAR, UserProfileCvEmbeddedEnum.IS_DELETED_MESSAGE.getKey());
        Update update = new Update().set(updateKey, false);
        mongoTemplate.updateMulti(new Query(queryKey), update, ConversationDoc.class);
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
    public void updateNowForUpdatedDate(List<ObjectId> ids, Date date) {
        super.updateNowForUpdatedDate(ids, date);
    }

    @Override
    public List<ConversationDoc> findNotHavePublicKeyByToken(Long userId, String accessToken, String publicKey, int limit) {
        return super.findNotHavePublicKeyByToken(userId, accessToken, publicKey, limit);
    }

    @Override
    public List<ConversationDoc> findByMessageCenterIdAndUserProfileIdWithTimestamp(ObjectId messageCenterId, Long userProfileId, Long timestamp, int size) {
        return super.findByMessageCenterIdAndUserProfileIdWithTimestamp(messageCenterId, userProfileId, timestamp, size);
    }

    @Override
    public long countByMessageCenterIdAndUserProfileIdWithTimestamp(ObjectId messageCenterId, Long userProfileId, Long timestamp) {
        return super.countByMessageCenterIdAndUserProfileIdWithTimestamp(messageCenterId, userProfileId, timestamp);
    }

    @Override
    public ConversationDoc findByIdAndUserProfileId(ObjectId id, Long userProfileId) {
        return super.findByIdAndUserProfileId(id, userProfileId);
    }

    @Override
    public List<ConversationDoc> findByMessageCenterIdAndUserProfileId(List<ObjectId> messageCenterIds, Long userProfile, List<String> includeFields) {
        return super.findByMessageCenterIdAndUserProfileId(messageCenterIds, userProfile, includeFields);
    }
}