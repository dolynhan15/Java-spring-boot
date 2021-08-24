package com.qooco.boost.data.mongo.services.impl;

import com.google.common.collect.ImmutableList;
import com.mongodb.WriteResult;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.enumeration.doc.MessageCenterDocEnum;
import com.qooco.boost.data.enumeration.embedded.UserProfileCvEmbeddedEnum;
import com.qooco.boost.data.enumeration.embedded.UserProfileEmbeddedEnum;
import com.qooco.boost.data.mongo.embedded.UserProfileBasicEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.repositories.MessageCenterDocRepository;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.qooco.boost.data.enumeration.MessageCenterType.AUTHORIZATION_CONVERSATION;
import static com.qooco.boost.data.enumeration.MessageCenterType.VACANCY_CONVERSATION;
import static com.qooco.boost.data.mongo.services.impl.Boot2MongoUtils.toWriteResult;

@Service
public class MessageCenterDocServiceImpl implements MessageCenterDocService {
    @Autowired
    private MessageCenterDocRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public MongoRepository<MessageCenterDoc, ObjectId> getRepository() {
        return repository;
    }

    @Override
    public List<MessageCenterDoc> findByTypeAndUserProfileAndTimestamp(List<Integer> types, long userProfileId, Long timestamp, int size) {
        Criteria typeCriteria = Criteria.where(MessageCenterDocEnum.TYPE.getKey()).in(types);
        Criteria userProfileCriteria = getCandidateInMessageCenterCriteria(userProfileId);
        Criteria criteria = new Criteria().andOperator(typeCriteria, userProfileCriteria);

        return getMessageCenterDocsWithPagination(criteria, timestamp, size);
    }

    @Override
    public long countByTypeAndUserProfileAndTimestamp(List<Integer> types, long userProfileId, Long timestamp) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(MessageCenterDocEnum.TYPE.getKey()).in(types),
                getCandidateInMessageCenterCriteria(userProfileId));
        Query query = initMessageCenterDocsWithPagination(criteria, timestamp);
        return mongoTemplate.count(query, MessageCenterDoc.class);
    }

    @Override
    public List<MessageCenterDoc> findByTypeAndUserProfile(List<Integer> types, long userProfileId, List<ObjectId> ids) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(MessageCenterDocEnum.TYPE.getKey()).in(types),
                Criteria.where(MessageCenterDocEnum.ID.getKey()).in(ids),
                getCandidateInMessageCenterCriteria(userProfileId));
        return getMessageCenterDocs(criteria);
    }


    private Criteria getCandidateInMessageCenterCriteria(long userProfileId) {
        Criteria userCriteria = new Criteria().andOperator(
                Criteria.where(UserProfileBasicEmbedded.Fields.userProfileId).is(userProfileId),
                UserProfileCvEmbeddedEnum.IS_NOT_DELETED_MESSAGE_CRITERIA);
        Criteria userAndType = new Criteria().andOperator(userCriteria,
                Criteria.where(UserProfileBasicEmbedded.Fields.userType).is(UserType.PROFILE));

        //TODO: Why or here
        return new Criteria().orOperator(
                Criteria.where(MessageCenterDocEnum.APPLIED_USER_PROFILES.getKey()).elemMatch(userCriteria),
                Criteria.where(MessageCenterDocEnum.APPOINTMENT_CANDIDATES.getKey()).elemMatch(userCriteria),
                Criteria.where(MessageCenterDocEnum.BOOST_HELPER_USER.getKey()).elemMatch(userAndType));
    }

    private List<MessageCenterDoc> getMessageCenterDocs(Criteria criteria) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, MessageCenterDocEnum.UPDATED_DATE.getKey()));
        return mongoTemplate.find(new Query(criteria).with(sort), MessageCenterDoc.class);
    }

    private Query initMessageCenterDocsWithPagination(Criteria criteria, Long timestamp) {
        Query query;
        if (Objects.nonNull(timestamp)) {
            Criteria timestampCriteria = Criteria.where(MessageCenterDocEnum.UPDATED_DATE.getKey()).lt(new Date(timestamp));
            Criteria messageCenterCriteria = new Criteria().andOperator(timestampCriteria, criteria);
            query = new Query(messageCenterCriteria);
        } else {
            query = new Query(criteria);
        }
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, MessageCenterDocEnum.UPDATED_DATE.getKey()),
                new Sort.Order(Sort.Direction.DESC, MessageCenterDocEnum.CREATED_DATE.getKey()));
        query.with(sort);
        return query;
    }

    private List<MessageCenterDoc> getMessageCenterDocsWithPagination(Criteria criteria, Long timestamp, int size) {
        Query query = initMessageCenterDocsWithPagination(criteria, timestamp);
        return mongoTemplate.find(query.limit(size), MessageCenterDoc.class);
    }

    @Override
    public List<MessageCenterDoc> findByUserProfileFromHotelAndTimestamp(long userProfileId, Long timestamp, int size) {
        Criteria criteriaRecruiter = getRecruiterInMessageCenterCriteria(userProfileId);
        return getMessageCenterDocsWithPagination(criteriaRecruiter, timestamp, size);
    }

    @Override
    public long countByUserProfileFromHotelAndTimestamp(long userProfileId, Long timestamp) {
        Criteria criteriaRecruiter = getRecruiterInMessageCenterCriteria(userProfileId);
        Query query = initMessageCenterDocsWithPagination(criteriaRecruiter, timestamp);
        return mongoTemplate.count(query, MessageCenterDoc.class);
    }

    @Override
    public List<MessageCenterDoc> findByUserProfileFromHotel(long userProfileId, List<ObjectId> ids) {
        Criteria messageCenterCriteria = Criteria.where(MessageCenterDocEnum.ID.getKey()).in(ids);
        Criteria criteriaRecruiter = getRecruiterInMessageCenterCriteria(userProfileId);
        Criteria criteria = new Criteria().andOperator(messageCenterCriteria, criteriaRecruiter);
        return getMessageCenterDocs(criteria);
    }

    private Criteria getRecruiterInMessageCenterCriteria(long userProfileId) {
        Criteria userCriteria = new Criteria().andOperator(
                Criteria.where(UserProfileEmbeddedEnum.USER_PROFILE_ID.getKey()).is(userProfileId),
                UserProfileCvEmbeddedEnum.IS_NOT_DELETED_MESSAGE_CRITERIA);
        Criteria userAndType = new Criteria().andOperator(userCriteria,
                Criteria.where(UserProfileEmbeddedEnum.USER_TYPE.getKey()).is(UserType.SELECT));

        Criteria contactPersonCriteria = Criteria.where(MessageCenterDoc.Fields.contactPersons).elemMatch(userCriteria);
        Criteria requestedJoinUserCriteria = Criteria.where(MessageCenterDoc.Fields.requestedJoinUsers).elemMatch(userCriteria);
        Criteria adminCriteria = Criteria.where(MessageCenterDoc.Fields.adminOfCompany).elemMatch(userCriteria);
        Criteria managerCriteria = Criteria.where(MessageCenterDoc.Fields.appointmentManagers).elemMatch(userCriteria);
        Criteria recruiterFreeChat = Criteria.where(MessageCenterDoc.Fields.freeChatRecruiter).elemMatch(userCriteria);
        Criteria boostHelperCriteria = Criteria.where(MessageCenterDoc.Fields.boostHelperUser).elemMatch(userAndType);

        return new Criteria().orOperator(
                contactPersonCriteria,
                requestedJoinUserCriteria,
                adminCriteria,
                managerCriteria,
                recruiterFreeChat,
                boostHelperCriteria
        );
    }

    @Override
    public MessageCenterDoc findByVacancy(Long vacancyId) {
        Criteria criteria = Criteria.where(MessageCenterDocEnum.VACANCY_ID.getKey()).is(vacancyId);
        return mongoTemplate.findOne(new Query(criteria), MessageCenterDoc.class);
    }

    @Override
    public List<ObjectId> findByVacancy(List<Long> vacancyIds) {
        if (CollectionUtils.isNotEmpty(vacancyIds)) {
            Criteria criteria = new Criteria().andOperator(
                    Criteria.where(MessageCenterDocEnum.VACANCY_ID.getKey()).in(vacancyIds),
                    MessageCenterDocEnum.IS_NOT_DELETED_CRITERIA);

            Query query = new Query(criteria);
            query.fields().include(MessageCenterDocEnum.ID.getKey());
            return mongoTemplate.find(query, MessageCenterDoc.class).stream()
                    .map(MessageCenterDoc::getId).distinct().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public MessageCenterDoc findMessageCenterForAuthorizationByCompany(long companyId) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(MessageCenterDocEnum.TYPE.getKey()).is(AUTHORIZATION_CONVERSATION.value()),
                Criteria.where(MessageCenterDocEnum.COMPANY_ID.getKey()).is(companyId));
        return mongoTemplate.findOne(new Query(criteria), MessageCenterDoc.class);
    }

    @Override
    public MessageCenterDoc findByTypeAndUserOfBoostHelper(int messageType, Long userId, int userType) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(MessageCenterDocEnum.IS_DELETED.getKey()).is(false),
                Criteria.where(MessageCenterDocEnum.TYPE.getKey()).is(messageType),
                Criteria.where(MessageCenterDocEnum.BOOST_HELPER_USER_USER_PROFILE_ID.getKey()).is(userId),
                Criteria.where(MessageCenterDocEnum.BOOST_HELPER_USER_USER_TYPE.getKey()).is(userType));
        return mongoTemplate.findOne(new Query(criteria), MessageCenterDoc.class);
    }

    @Override
    public List<MessageCenterDoc> findMessageCenterForVacancyByContactUserProfileAndCompany(long userProfileId, long companyId) {
        Criteria staffCriteria = new Criteria().orOperator(
                Criteria.where(MessageCenterDocEnum.CONTACT_PERSONS_USER_PROFILE_ID.getKey()).is(userProfileId),
                Criteria.where(MessageCenterDocEnum.APPOINTMENT_MANAGERS_USER_PROFILE_ID.getKey()).is(userProfileId)
        );

        Criteria criteria = new Criteria().andOperator(
                Criteria.where(MessageCenterDocEnum.TYPE.getKey()).is(VACANCY_CONVERSATION.value()),
                Criteria.where(MessageCenterDocEnum.VACANCY_COMPANY_ID.getKey()).is(companyId),
                staffCriteria);
        return getMessageCenterDocs(criteria);
    }

    @Override
    public WriteResult updateNumberOfCandidate(MessageCenterDoc messageCenterDoc) {
        Criteria criteria = Criteria.where(MessageCenterDocEnum.ID.getKey()).is(messageCenterDoc.getId());
        Update update = new Update().set(MessageCenterDocEnum.NUMBER_OF_CANDIDATE.getKey(), messageCenterDoc.getNumberOfCandidate());
        return toWriteResult(mongoTemplate.updateFirst(new Query(criteria), update, MessageCenterDoc.class));
    }

    @Override
    public void updateNowForUpdatedDate(List<ObjectId> ids, Date date) {
        if (CollectionUtils.isNotEmpty(ids) && Objects.nonNull(date)) {
            Criteria criteria = Criteria.where(MessageCenterDocEnum.ID.getKey()).in(ids);
            Update update = new Update().set(MessageCenterDocEnum.UPDATED_DATE.getKey(), date);
            mongoTemplate.updateMulti(new Query(criteria), update, MessageCenterDoc.class);
        }
    }

    @Override
    public void updateFreeChatParticipant(ObjectId id, UserProfileCvEmbedded recruiter, UserProfileCvEmbedded candidate) {

        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MessageCenterDoc.class);
        Optional.ofNullable(recruiter).ifPresent(it -> {
            Criteria recruiterFreeChatCriteria = new Criteria().andOperator(
                    Criteria.where(MessageCenterDocEnum.ID.getKey()).is(id),
                    Criteria.where(MessageCenterDocEnum.FREE_CHAT_RECRUITER_USER_PROFILE_ID.getKey()).nin(recruiter.getUserProfileId()));
            Update updater = new Update().push(MessageCenterDocEnum.FREE_CHAT_RECRUITER.getKey(), recruiter);
            bulkOps.updateMulti(new Query(recruiterFreeChatCriteria), updater);
        });

        Optional.ofNullable(candidate).ifPresent(it -> {
            Criteria candidateFreeChatCriteria = new Criteria().andOperator(
                    Criteria.where(MessageCenterDocEnum.ID.getKey()).is(id),
                    Criteria.where(MessageCenterDocEnum.FREE_CHAT_CANDIDATE_USER_PROFILE_ID.getKey()).nin(candidate.getUserProfileId()));
            Update updater = new Update().push(MessageCenterDocEnum.FREE_CHAT_CANDIDATE.getKey(), candidate);
            bulkOps.updateMulti(new Query(candidateFreeChatCriteria), updater);
        });
        bulkOps.execute();
    }

    @Override
    public void updateAdminsOfCompany(Long companyId, UserProfileCvEmbedded admin, String oldRoleName, String newRoleName) {
        Update updater = new Update();
        Criteria criteria;
        String adminOfCompanyKey = StringUtil.append(MessageCenterDocEnum.ADMIN_OF_COMPANY.getKey(), Constants.DOT, UserProfileEmbeddedEnum.USER_PROFILE_ID.getKey());

        if (CompanyRole.ADMIN.getName().equals(newRoleName)) {
            criteria = new Criteria().andOperator(
                    Criteria.where(MessageCenterDocEnum.COMPANY_ID.getKey()).is(companyId),
                    Criteria.where(adminOfCompanyKey).nin(admin.getUserProfileId()));
            updater.push(MessageCenterDocEnum.ADMIN_OF_COMPANY.getKey(), admin);

        } else {
            criteria = new Criteria().andOperator(
                    Criteria.where(MessageCenterDocEnum.COMPANY_ID.getKey()).is(companyId),
                    Criteria.where(adminOfCompanyKey).is(admin.getUserProfileId()));

            Criteria subCriteria = Criteria.where(UserProfileEmbeddedEnum.USER_PROFILE_ID.getKey()).is(admin.getUserProfileId());
            updater.pull(MessageCenterDocEnum.ADMIN_OF_COMPANY.getKey(), new Query(subCriteria).getQueryObject());
        }
        mongoTemplate.updateMulti(new Query(criteria), updater, MessageCenterDoc.class);
    }

    //TODO: Will remove in next sprint after release production
    @Deprecated
    @Override
    public void updateAdminsOfCompany(Map<Long, List<UserProfileCvEmbedded>> adminOfCompanyMap) {
        if (MapUtils.isNotEmpty(adminOfCompanyMap)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MessageCenterDoc.class);
            adminOfCompanyMap.forEach((companyId, admins) -> {
                Criteria criteria = Criteria.where(MessageCenterDocEnum.COMPANY_ID.getKey()).is(companyId);
                Update updater = new Update();
                updater.set(MessageCenterDocEnum.ADMIN_OF_COMPANY.getKey(), admins);
                bulkOps.updateMulti(new Query(criteria), updater);
            });
            bulkOps.execute();
        }
    }

    @Override
    public void deleteMessageCenterOfUser(ObjectId messageCenterId, Long userProfileId, boolean isHotelApp) {
        List<String> messageEnums;
        if (isHotelApp) {
            messageEnums = ImmutableList.of(
                    MessageCenterDocEnum.CONTACT_PERSONS.getKey(),
                    MessageCenterDocEnum.APPOINTMENT_MANAGERS.getKey(),
                    MessageCenterDocEnum.REQUESTED_JOIN_USERS.getKey(),
                    MessageCenterDocEnum.ADMIN_OF_COMPANY.getKey(),
                    MessageCenterDocEnum.FREE_CHAT_RECRUITER.getKey()
            );
        } else {
            messageEnums = ImmutableList.of(
                    MessageCenterDocEnum.APPLIED_USER_PROFILES.getKey(),
                    MessageCenterDocEnum.APPOINTMENT_CANDIDATES.getKey(),
                    MessageCenterDocEnum.FREE_CHAT_CANDIDATE.getKey()
            );
        }
        messageEnums.forEach(it -> updateIsDeletedMessage(messageCenterId, userProfileId, it));
    }

    @Override
    public void syncIsDeletedMessageInMessageCenterDoc() {
        //TODO: Move this list enum to use with enum in deleteMessageCenterOfUser()
        //Why delete both profile and select app
        List<String> messageEnums = ImmutableList.of(
                MessageCenterDocEnum.APPLIED_USER_PROFILES.getKey(),
                MessageCenterDocEnum.CONTACT_PERSONS.getKey(),
                MessageCenterDocEnum.APPOINTMENT_CANDIDATES.getKey(),
                MessageCenterDocEnum.APPOINTMENT_MANAGERS.getKey(),
                MessageCenterDocEnum.REQUESTED_JOIN_USERS.getKey(),
                MessageCenterDocEnum.ADMIN_OF_COMPANY.getKey(),
                MessageCenterDocEnum.FREE_CHAT_RECRUITER.getKey(),
                MessageCenterDocEnum.FREE_CHAT_CANDIDATE.getKey()
        );
        messageEnums.forEach(this::syncDataIsDeletedMessage);
    }

    private void syncDataIsDeletedMessage(String prefix) {
        Criteria isDeletedMessageKey = Criteria.where(UserProfileCvEmbeddedEnum.IS_DELETED_MESSAGE.getKey()).exists(false);
        Criteria userKey = Criteria.where(UserProfileEmbeddedEnum.USER_PROFILE_ID.getKey()).exists(true);
        Criteria participantsCriteria = new Criteria().andOperator(isDeletedMessageKey, userKey);
        Criteria queryKey = Criteria.where(prefix).elemMatch(participantsCriteria);

        String updateKey = StringUtil.append(prefix, Constants.DOLLAR, UserProfileCvEmbeddedEnum.IS_DELETED_MESSAGE.getKey());
        Update update = new Update().set(updateKey, false);
        mongoTemplate.updateMulti(new Query(queryKey), update, MessageCenterDoc.class);
    }

    private void updateIsDeletedMessage(ObjectId messageCenterId, Long userProfileId, String prefix) {
        Criteria isDeletedCriteria = Criteria.where(MessageCenterDocEnum.IS_DELETED.getKey()).is(false);
        Criteria messageCenterCriteria = Criteria.where(MessageCenterDocEnum.ID.getKey()).is(messageCenterId);

        String userProfileIdKey = UserProfileEmbeddedEnum.USER_PROFILE_ID.getKey();
        String queryKey = StringUtil.append(prefix, Constants.DOT, userProfileIdKey);
        Criteria userProfileCriteria = Criteria.where(queryKey).is(userProfileId);

        Criteria criteria = new Criteria().andOperator(messageCenterCriteria, userProfileCriteria, isDeletedCriteria);

        String isDeletedMessageKey = UserProfileCvEmbeddedEnum.IS_DELETED_MESSAGE.getKey();
        String updatedKey = StringUtil.append(prefix, Constants.DOLLAR, isDeletedMessageKey);
        Update update = new Update().set(updatedKey, true);
        mongoTemplate.updateMulti(new Query(criteria), update, MessageCenterDoc.class);
    }
}
