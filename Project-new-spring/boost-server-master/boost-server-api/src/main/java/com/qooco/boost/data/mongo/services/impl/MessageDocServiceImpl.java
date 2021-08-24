package com.qooco.boost.data.mongo.services.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.enumeration.doc.MessageDocEnum;
import com.qooco.boost.data.enumeration.embedded.UserProfileCvEmbeddedEnum;
import com.qooco.boost.data.enumeration.embedded.UserProfileEmbeddedEnum;
import com.qooco.boost.data.model.Conversation;
import com.qooco.boost.data.model.MessageGroupByAppointmentDetail;
import com.qooco.boost.data.model.ObjectLatest;
import com.qooco.boost.data.model.count.LongCount;
import com.qooco.boost.data.model.count.ObjectIdCount;
import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.mongo.repositories.MessageDocRepository;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.impl.abstracts.MessageDocAbstract;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;
import static java.util.Optional.ofNullable;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
public class MessageDocServiceImpl extends MessageDocAbstract<MessageDoc> implements MessageDocService {
    protected Logger logger = LogManager.getLogger(MessageDocServiceImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MessageDocRepository repository;
    @Autowired
    private MessageCenterDocService messageCenterDocService;
    @Autowired
    private ConversationDocService conversationDocService;

    @Override
    public MessageDoc findById(ObjectId id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public MessageDoc save(MessageDoc messageDoc, boolean isEditUpdatedDate) {
        Date now = DateUtils.toServerTimeForMongo();
        if (isEditUpdatedDate) {
            conversationDocService.updateNowForUpdatedDate(Lists.newArrayList(messageDoc.getConversationId()), now);
            messageCenterDocService.updateNowForUpdatedDate(Lists.newArrayList(messageDoc.getMessageCenterId()), now);
        }
        return repository.save(messageDoc);
    }

    @Override
    public List<MessageDoc> save(List<MessageDoc> messageDocs) {
        Date now = DateUtils.toServerTimeForMongo();
        List<ObjectId> conversationIds = messageDocs.stream().map(MessageDoc::getConversationId).distinct().collect(Collectors.toList());
        List<ObjectId> messageCenterIds = messageDocs.stream().map(MessageDoc::getMessageCenterId).distinct().collect(Collectors.toList());
        conversationDocService.updateNowForUpdatedDate(conversationIds, now);
        messageCenterDocService.updateNowForUpdatedDate(messageCenterIds, now);
        return repository.saveAll(messageDocs);
    }

    @Override
    public List<MessageDoc> findByIds(List<ObjectId> ids) {
        Criteria criteria = Criteria.where(MessageDocEnum.ID.getKey()).in(ids);
        return mongoTemplate.find(new Query(criteria), MessageDoc.class);
    }

    @Override
    public List<MessageDoc> findByAppointmentDetailId(Long appointmentDetailId) {
        Criteria criteria = Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_ID.getKey()).is(appointmentDetailId);
        return mongoTemplate.find(new Query(criteria), MessageDoc.class);
    }

    @Override
    public List<ObjectIdCount> countUnreadMessageGroupByMessageCenter(List<ObjectId> conversationIds, Long userProfileId, int receiveInApp) {
        return super.countUnreadMessageGroupByKey(MessageBase.Fields.messageCenterId, conversationIds, userProfileId, receiveInApp);
    }

    @Override
    public List<MessageDoc> findByJoinCompanyRequestId(long joinCompanyRequestId) {
        Criteria typeCriteria = Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.AUTHORIZATION_MESSAGE);
        Criteria authorizationCriteria = Criteria.where(MessageDocEnum.AUTHORIZATION_MESSAGE_ID.getKey()).is(joinCompanyRequestId);
        Criteria criteria = new Criteria().andOperator(typeCriteria, authorizationCriteria, MessageDocEnum.IS_NOT_DELETED_CRITERIA);
        return mongoTemplate.find(new Query(criteria), MessageDoc.class);
    }

    @Override
    public MessageDoc findByRecipientAndStaffAndType(Long recipientId, StaffEmbedded staff, int type) {

        Criteria recipientUserProfileCriteria = Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).is(recipientId);
        Criteria staffCriteria = Criteria.where(MessageDocEnum.STAFF_ID.getKey()).is(staff.getId());
        Criteria typeCriteria = Criteria.where(MessageDocEnum.TYPE.getKey()).is(type);

        Criteria criteria = new Criteria().andOperator(recipientUserProfileCriteria, staffCriteria, typeCriteria, recipientIsNotDeletedMessageCriteria, MessageDocEnum.IS_NOT_DELETED_CRITERIA);
        return mongoTemplate.findOne(new Query(criteria), MessageDoc.class);
    }

    @Override
    public void softDeleteMessageByConversationIds(List<ObjectId> conversationIds) {
        ofNullable(conversationIds)
                .filter(CollectionUtils::isNotEmpty)
                .ifPresent(it -> {
                    Criteria conversationIdCriteria = Criteria.where(MessageDocEnum.CONVERSATION_ID.getKey()).in(conversationIds);
                    Update update = new Update().set(MessageDocEnum.IS_DELETED.getKey(), true);
                    mongoTemplate.updateMulti(new Query(conversationIdCriteria), update, MessageDoc.class);
                });
    }

    @Override
    public List<LongCount> countUnreadMessageByUserProfileCvId(List<Long> userProfileIds, int receiveInApp, long senderId) {
        Criteria typeCriteria = countUnreadMessage(senderId, receiveInApp);
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(MessageDocEnum.SENDER_USER_PROFILE_ID.getKey()).is(userProfileIds),
                MessageDocEnum.IS_NOT_DELETED_CRITERIA, typeCriteria);

        return countMessageGroupByKey(criteria, MessageDocEnum.SENDER_USER_PROFILE_ID.getKey(), LongCount.class);
    }

    @Override
    public long countUserSendUnreadMessageByUserProfileId(Long userProfileId, int receiveInApp) {
        var criteria = countUnreadMessage(userProfileId, receiveInApp);
        return mongoTemplate.count(new Query(criteria), MessageDoc.class);
    }

    protected Criteria countUnreadMessage(long userProfileId, int receiveInApp) {
        var typeCriteria = initTypeUnreadCriteriaByRecipientAndReceiveInApp(userProfileId, receiveInApp);
        var criteriaNormalCount = new Criteria().andOperator(typeCriteria, MessageDocEnum.IS_NOT_DELETED_CRITERIA);

        if (receiveInApp == SELECT_APP.value()) {
            var typeActionCriteria = initUnreadForActionMessage(userProfileId, receiveInApp);
            var criteriaActionMessageCount = new Criteria().andOperator(typeActionCriteria, MessageDocEnum.IS_NOT_DELETED_CRITERIA);
            return new Criteria().orOperator(criteriaNormalCount, criteriaActionMessageCount);
        } else {
            return criteriaNormalCount;
        }
    }

    private Criteria initTypeUnreadCriteriaByRecipientAndReceiveInApp(Long recipients, int receiveInApp) {
        var typeCriteria = initTypeUnreadCriteria();
        return new Criteria().andOperator(
                Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).in(recipients),
                recipientIsNotDeletedMessageCriteria,
                Criteria.where(MessageDocEnum.RECEIVE_IN_APP.getKey()).in(ImmutableList.of(receiveInApp)),
                typeCriteria
        );
    }

    protected Criteria initTypeUnreadCriteria() {
        var result = new ArrayList<Criteria>();
        result.addAll(super.initUnreadCriteria());
        var statusCriteria = new Criteria().orOperator(
                Criteria.where(MessageBase.Fields.status).in(MessageConstants.MESSAGE_STATUS_SENT),
                Criteria.where(MessageBase.Fields.status).in(MessageConstants.MESSAGE_STATUS_RECEIVED));

        var applicantMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.APPLICANT_MESSAGE),
                Criteria.where(MessageDocEnum.APPLIED_MESSAGE_RESPONSE_STATUS.getKey()).is(MessageConstants.APPLIED_STATUS_PENDING));

        var authorizationMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.AUTHORIZATION_MESSAGE),
                Criteria.where(MessageDocEnum.AUTHORIZATION_MESSAGE_RESPONSE_STATUS.getKey()).is(MessageConstants.JOIN_COMPANY_REQUEST_STATUS_PENDING));

        var assignmentMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.ASSIGNMENT_ROLE_MESSAGE),
                statusCriteria);

        var appointmentMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.APPOINTMENT_MESSAGE),
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_RESPONSE_STATUS.getKey()).is(MessageConstants.APPOINTMENT_STATUS_PENDING),
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_TO_DATE.getKey()).gt(DateUtils.toServerTimeForMongo()),
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_DETAIL_STATUS.getKey()).nin(AppointmentStatus.getCancelStatus()));

        var cancelAppointment = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.APPOINTMENT_MESSAGE),
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_RESPONSE_STATUS.getKey()).is(MessageConstants.APPOINTMENT_STATUS_PENDING),
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_DETAIL_STATUS.getKey()).is(AppointmentStatus.getCancelStatus()),
                statusCriteria);

        var expiredAppointment = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.APPOINTMENT_MESSAGE),
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_RESPONSE_STATUS.getKey()).is(MessageConstants.APPOINTMENT_STATUS_PENDING),
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_TO_DATE.getKey()).lte(DateUtils.toServerTimeForMongo()),
                statusCriteria);

        var applicantAppointmentMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.APPOINTMENT_APPLICANT_MESSAGE),
                statusCriteria);

        var cancelAppointmentMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.APPOINTMENT_CANCEL_MESSAGE),
                statusCriteria);

        var congratulationMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.CONGRATULATION_MESSAGE),
                statusCriteria);
        var suspendedMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.SUSPENDED_VACANCY),
                statusCriteria);

        var changedContactPersonMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.CHANGE_CONTACT_MESSAGE),
                statusCriteria);

        var inActiveMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.INACTIVE_VACANCY),
                statusCriteria);

        var boostHelperMessage = new Criteria().andOperator(
                Criteria.where(MessageBase.Fields.type).is(MessageConstants.BOOST_HELPER_MESSAGE),
                statusCriteria);

        result.addAll(Arrays.asList(applicantMessage, authorizationMessage, assignmentMessage,
                appointmentMessage, applicantAppointmentMessage, cancelAppointmentMessage,
                cancelAppointment, expiredAppointment, congratulationMessage, suspendedMessage,
                changedContactPersonMessage, inActiveMessage, boostHelperMessage));

        return new Criteria().orOperator(result.toArray(new Criteria[0]));
    }

    private Criteria initUnreadForActionMessage(Long userProfileId, int receiveInApp) {
        Criteria typeCriteria = new Criteria();
        if (receiveInApp == MessageConstants.RECEIVE_IN_HOTEL_APP) {

            Criteria messageSeenCriteria = Criteria.where(MessageDocEnum.STATUS.getKey()).is(MessageConstants.MESSAGE_STATUS_SEEN);
            Criteria senderCriteria = Criteria.where(MessageDocEnum.SENDER_USER_PROFILE_ID.getKey()).is(userProfileId);

            Criteria receiveInHotelCriteria = Criteria.where(MessageDocEnum.RECEIVE_IN_APP.getKey()).is(MessageConstants.RECEIVE_IN_HOTEL_APP);
            Criteria receiveInCareerCriteria = Criteria.where(MessageDocEnum.RECEIVE_IN_APP.getKey()).is(MessageConstants.RECEIVE_IN_CAREER_APP);

            Criteria applicantMessage = new Criteria().andOperator(
                    Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.APPLICANT_MESSAGE),
                    receiveInCareerCriteria, senderCriteria, senderIsNotDeletedMessageCriteria, messageSeenCriteria,
                    Criteria.where(MessageDocEnum.APPLIED_MESSAGE_RESPONSE_STATUS.getKey()).nin(MessageConstants.APPLIED_STATUS_PENDING),
                    Criteria.where(MessageDocEnum.APPLIED_MESSAGE_STATUS.getKey()).in(UNREAD_STATUS)
            );

            Criteria appointmentMessage = new Criteria().andOperator(
                    Criteria.where(MessageDocEnum.TYPE.getKey()).in(MessageConstants.APPOINTMENT_MESSAGE),
                    receiveInCareerCriteria, senderCriteria, senderIsNotDeletedMessageCriteria, messageSeenCriteria,
                    Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_RESPONSE_STATUS.getKey()).nin(MessageConstants.APPOINTMENT_STATUS_PENDING),
                    Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_STATUS.getKey()).in(UNREAD_STATUS)
            );

            Criteria authorizationMessage = new Criteria().andOperator(
                    Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.AUTHORIZATION_MESSAGE),
                    receiveInHotelCriteria, senderCriteria, senderIsNotDeletedMessageCriteria, messageSeenCriteria,
                    Criteria.where(MessageDocEnum.AUTHORIZATION_MESSAGE_RESPONSE_STATUS.getKey()).nin(MessageConstants.JOIN_COMPANY_REQUEST_STATUS_PENDING),
                    Criteria.where(MessageDocEnum.AUTHORIZATION_MESSAGE_STATUS.getKey()).in(UNREAD_STATUS)
            );

            Criteria assignRoleMessage = new Criteria().andOperator(
                    Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.ASSIGNMENT_ROLE_MESSAGE),
                    receiveInHotelCriteria, recipientIsNotDeletedMessageCriteria, messageSeenCriteria,
                    Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).is(userProfileId),
                    Criteria.where(MessageDocEnum.STAFF_MESSAGE_STATUS.getKey()).nin(MessageConstants.JOIN_COMPANY_REQUEST_STATUS_PENDING),
                    Criteria.where(MessageDocEnum.STAFF_MESSAGE_STATUS.getKey()).in(UNREAD_STATUS)
            );

            typeCriteria.orOperator(applicantMessage, authorizationMessage, assignRoleMessage, appointmentMessage);
        }
        return typeCriteria;
    }

    @Override
    public List<MessageDoc> findFirstMessage(List<ObjectId> conversationIds) {
        Criteria conversationCriteria1 = Criteria.where(MessageDocEnum.CONVERSATION_ID.getKey()).in(conversationIds);

        Aggregation aggregation = newAggregation(
                Aggregation.match(conversationCriteria1),
                Aggregation.group(MessageDocEnum.CONVERSATION_ID.getKey()).min(MessageDocEnum.ID.getKey()).as(Conversation.Fields.messageId));

        AggregationResults<Conversation> groupResults = mongoTemplate.aggregate(aggregation, MessageDoc.class, Conversation.class);

        List<Conversation> result = groupResults.getMappedResults();
        if (CollectionUtils.isNotEmpty(result)) {
            return findByIds(result.stream().map(Conversation::getMessageId).collect(Collectors.toList()));
        }
        return null;
    }

    @Override
    public List<ObjectIdCount> countUnreadMessageGroupByConversation(ObjectId messageCenterId, Long userProfileId, int receiveInApp) {
        return super.countUnreadMessageGroupByConversation(messageCenterId, userProfileId, receiveInApp);
    }

    @Override
    public List<ObjectIdCount> countMessageGroupByConversation(List<Long> appointmentDetailIds) {
        var criteria = new Criteria().andOperator(
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_ID.getKey()).in(appointmentDetailIds),
                MessageDocEnum.IS_NOT_DELETED_CRITERIA);

        return countMessageGroupByKey(criteria, MessageDocEnum.CONVERSATION_ID.getKey(), ObjectIdCount.class);
    }

    @Override
    public long countUnreadMessageByUserProfileId(ObjectId conversationId, Long userProfileId, int receiveInApp) {
        return super.countUnreadMessageByUserProfileId(conversationId, userProfileId, receiveInApp);
    }

    @Override
    public void updateSeenForOlderMessage(MessageDoc messageDoc, boolean isUpdateAppointmentResponseStatus) {
        // TODO Update logic unread
        updateStatusForOlderActionMessage(messageDoc, UNREAD_STATUS
                , MessageConstants.MESSAGE_STATUS_SEEN, isUpdateAppointmentResponseStatus);
    }

    @Override
    public void updateReceivedForOlderMessage(MessageDoc messageDoc, boolean isUpdateAppointmentResponseStatus) {
        // TODO Update logic unread
        updateStatusForOlderActionMessage(messageDoc, ImmutableList.of(MessageConstants.MESSAGE_STATUS_SENT)
                , MessageConstants.MESSAGE_STATUS_RECEIVED, isUpdateAppointmentResponseStatus);
    }

    @Override
    public List<MessageDoc> findByConversationIdAndSizeAndTimestamp(ObjectId conversationId, Date timestamp, int size) {
        return super.findByConversationIdAndSizeAndTimestamp(conversationId, timestamp, size);
    }

    @Override
    public List<ObjectIdCount> countUnreadMessageGroupByKey(String key, long userProfileId, int receiveInApp) {
        return super.countUnreadMessageGroupByKey(key, userProfileId, receiveInApp);
    }

    @Override
    public List<MessageDoc> findApplicantMessageBySenderAndTypeAndVacancy(Long userProfileId, List<Long> vacancyIds, List<Integer> responseStatus) {
        Criteria availableCriteria = new Criteria().orOperator(
                Criteria.where(MessageDocEnum.APPLIED_MESSAGE_IS_AVAILABLE.getKey()).is(true),
                Criteria.where(MessageDocEnum.APPLIED_MESSAGE_IS_AVAILABLE.getKey()).exists(false));

        Criteria criteria = new Criteria().andOperator(
                Criteria.where(MessageDocEnum.SENDER_USER_PROFILE_ID.getKey()).is(userProfileId),
                senderIsNotDeletedMessageCriteria,
                Criteria.where(MessageDocEnum.APPLIED_MESSAGE_VACANCY_ID.getKey()).in(vacancyIds),
                Criteria.where(MessageDocEnum.APPLIED_MESSAGE_RESPONSE_STATUS.getKey()).in(responseStatus),
                Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.APPLICANT_MESSAGE),
                availableCriteria);
        return mongoTemplate.find(new Query(criteria), MessageDoc.class);
    }

    @Override
    public List<MessageDoc> findInterestedApplicantMessage(VacancyDoc vacancyDoc) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(MessageDocEnum.IS_DELETED.getKey()).is(false),
                Criteria.where(MessageDocEnum.COMPANY_ID.getKey()).is(vacancyDoc.getCompany().getId()),
                Criteria.where(MessageDocEnum.APPLIED_MESSAGE_VACANCY_ID.getKey()).is(vacancyDoc.getId()),
                Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.APPLICANT_MESSAGE),
                Criteria.where(MessageDocEnum.APPLIED_MESSAGE_RESPONSE_STATUS.getKey()).is(MessageConstants.APPLIED_STATUS_INTERESTED));
        return mongoTemplate.find(new Query(criteria), MessageDoc.class);
    }

    @Override
    public List<MessageDoc> findInterestedApplicantOrApplicationAppointmentMessage(VacancyDoc vacancyDoc) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(MessageDocEnum.IS_DELETED.getKey()).is(false),
                Criteria.where(MessageDocEnum.COMPANY_ID.getKey()).is(vacancyDoc.getCompany().getId()),
                Criteria.where(MessageDocEnum.APPLIED_MESSAGE_VACANCY_ID.getKey()).is(vacancyDoc.getId()),
                new Criteria().orOperator(
                        new Criteria().andOperator(Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.APPLICANT_MESSAGE),
                                Criteria.where(MessageDocEnum.APPLIED_MESSAGE_RESPONSE_STATUS.getKey()).is(MessageConstants.APPLIED_STATUS_INTERESTED)),
                        Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.APPOINTMENT_APPLICANT_MESSAGE))
        );
        return mongoTemplate.find(new Query(criteria), MessageDoc.class);
    }

    @Override
    public List<Long> getVacancyHaveAppointmentDetailByRecipient(Long userProfileId, List<Long> vacancyIds, List<Integer> status) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).is(userProfileId),
                recipientIsNotDeletedMessageCriteria,
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_VACANCY_ID.getKey()).in(vacancyIds),
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_DETAIL_STATUS.getKey()).in(status),
                Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.APPOINTMENT_MESSAGE));

        return countMessageGroupByKey(criteria, MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_VACANCY_ID.getKey(), LongCount.class)
                .stream().map(LongCount::getId).collect(Collectors.toList());
    }

    @Override
    public List<ObjectId> findMessageCenterHavingClosedMessage(List<ObjectId> messageCenterIds, Long userProfileId) {

        Criteria messageCenterIdCriteria = Criteria.where(MessageDocEnum.MESSAGE_CENTER_ID.getKey()).in(messageCenterIds);
        Criteria typeCriteria = Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.INACTIVE_VACANCY);
        Criteria recipientCriteria = Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).is(userProfileId);

        Criteria criteria = new Criteria().andOperator(messageCenterIdCriteria, typeCriteria, recipientCriteria, recipientIsNotDeletedMessageCriteria, MessageDocEnum.IS_NOT_DELETED_CRITERIA);

        List<MessageDoc> messageDocs = mongoTemplate.find(new Query(criteria), MessageDoc.class);
        List<ObjectId> messageCenterIdResults = messageDocs.stream().map(MessageDoc::getMessageCenterId).collect(Collectors.toList());
        messageCenterIdResults = messageCenterIdResults.stream().distinct().collect(Collectors.toList());
        return messageCenterIdResults;
    }

    @Override
    public long countByConversationIdAndSizeAndTimestamp(ObjectId conversationId, Date timestamp) {
        return super.countByConversationIdAndSizeAndTimestamp(conversationId, timestamp);
    }

    @Override
    public List<ObjectLatest> getLatestUpdatedDateByMessageCenterIds(List<ObjectId> messageCenterIds, Long userProfileId, int receiveInApp) {
        return super.getLatestUpdatedDateByKeyId(MessageBase.Fields.messageCenterId, messageCenterIds, userProfileId, receiveInApp);
    }

    @Override
    public List<ObjectLatest> getLatestUpdatedDateByConversationIds(List<ObjectId> conversationIds, Long userProfileId, int receiveInApp) {
        return super.getLatestUpdatedDateByKeyId(MessageBase.Fields.conversationId, conversationIds, userProfileId, receiveInApp);
    }

    private <T> List<T> countMessageGroupByKey(Criteria criteria, String keyId, Class<T> type) {
        Aggregation aggregation = newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(keyId).count().as(LongCount.Fields.total),
                Aggregation.sort(Sort.Direction.DESC, LongCount.Fields.total));

        AggregationResults<T> groupResults = mongoTemplate.aggregate(aggregation, MessageDoc.class, type);
        return groupResults.getMappedResults();
    }


    private void updateStatusForOlderActionMessage(MessageDoc message, List<Integer> statusCondition, int statusUpdate, boolean isUpdateAppointmentResponseStatus) {
        Query query;
        Update update;
        ObjectId conversationId = message.getConversationId();
        AtomicLong recipientId = new AtomicLong();
        if (isNotResponseMessageType(message.getType())) {
            recipientId.set(message.getRecipient().getUserProfileId());
        } else {
            ofNullable(message.getAppliedMessage()).ifPresent(m -> {
                if (m.getResponseStatus() == MessageConstants.APPLIED_STATUS_PENDING) {
                    recipientId.set(message.getRecipient().getUserProfileId());
                } else {
                    recipientId.set(message.getSender().getUserProfileId());
                }
            });
            ofNullable(message.getAuthorizationMessage()).ifPresent(m -> {
                if (m.getResponseStatus() == MessageConstants.JOIN_COMPANY_REQUEST_STATUS_PENDING) {
                    recipientId.set(message.getRecipient().getUserProfileId());
                } else {
                    recipientId.set(message.getSender().getUserProfileId());
                }
            });
            ofNullable(message.getAppointmentDetailMessage()).ifPresent(m -> {
                if (m.getResponseStatus() == AppointmentStatus.PENDING.getValue()) {
                    recipientId.set(message.getRecipient().getUserProfileId());
                } else {
                    recipientId.set(message.getSender().getUserProfileId());
                }
            });
        }
        Criteria criteriaNormalUpdate = initCriteriaUpdateStatusForReadOnlyMessage(conversationId, recipientId.get(), message.getCreatedDate());
        Criteria criteriaResponseUpdate = initCriteriaUpdateStatusForActionResponse(conversationId, recipientId.get(), message.getCreatedDate());
        Criteria criteria = new Criteria().andOperator(Criteria.where(MessageDocEnum.STATUS.getKey()).in(statusCondition), criteriaNormalUpdate);

        query = new Query().addCriteria(criteria);
        update = new Update();
        update.set(MessageDocEnum.STATUS.getKey(), statusUpdate);
        mongoTemplate.updateMulti(query, update, MessageDoc.class);

        Criteria applicantResponse = Criteria.where(MessageDocEnum.APPLIED_MESSAGE_RESPONSE_STATUS.getKey()).nin(MessageConstants.APPLIED_STATUS_PENDING);
        criteria = new Criteria()
                .andOperator(Criteria.where(MessageDocEnum.APPLIED_MESSAGE_STATUS.getKey()).in(statusCondition)
                        , criteriaResponseUpdate, applicantResponse);
        query = new Query().addCriteria(criteria);
        update = new Update();
        update.set(MessageDocEnum.APPLIED_MESSAGE_STATUS.getKey(), statusUpdate);
        mongoTemplate.updateMulti(query, update, MessageDoc.class);

        Criteria authorizationResponse = Criteria.where(MessageDocEnum.AUTHORIZATION_MESSAGE_RESPONSE_STATUS.getKey()).nin(MessageConstants.APPLIED_STATUS_PENDING);

        criteria = new Criteria();
        criteria.andOperator(Criteria.where(MessageDocEnum.AUTHORIZATION_MESSAGE_STATUS.getKey()).in(statusCondition)
                , criteriaResponseUpdate, authorizationResponse);

        query = new Query().addCriteria(criteria);
        update = new Update();
        update.set(MessageDocEnum.AUTHORIZATION_MESSAGE_STATUS.getKey(), statusUpdate);
        mongoTemplate.updateMulti(query, update, MessageDoc.class);

        criteria = new Criteria()
                .andOperator(Criteria.where(MessageDocEnum.STAFF_MESSAGE_STATUS.getKey()).in(statusCondition)
                        , criteriaNormalUpdate);
        query = new Query().addCriteria(criteria);
        update = new Update();
        update.set(MessageDocEnum.STAFF_MESSAGE_STATUS.getKey(), statusUpdate);
        mongoTemplate.updateMulti(query, update, MessageDoc.class);

        if (isUpdateAppointmentResponseStatus) {
            Criteria appointmentResponse = Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_RESPONSE_STATUS.getKey()).nin(MessageConstants.APPOINTMENT_STATUS_PENDING);
            criteria = new Criteria();
            criteria.andOperator(Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_STATUS.getKey()).in(statusCondition),
                    criteriaResponseUpdate, appointmentResponse);

            query = new Query().addCriteria(criteria);
            update = new Update();
            update.set(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_STATUS.getKey(), statusUpdate);
            mongoTemplate.updateMulti(query, update, MessageDoc.class);
        }
    }

    private Criteria initCriteriaUpdateStatusForActionResponse(ObjectId conversationId, long recipientUserId, Date createdDate) {
        return new Criteria().andOperator(
                Criteria.where(MessageDocEnum.CONVERSATION_ID.getKey()).is(conversationId),
                Criteria.where(MessageDocEnum.SENDER_USER_PROFILE_ID.getKey()).is(recipientUserId),
                Criteria.where(MessageDocEnum.CREATED_DATE.getKey()).lt(createdDate),
                senderIsNotDeletedMessageCriteria,
                MessageDocEnum.IS_NOT_DELETED_CRITERIA
        );
    }


    @Override
    public MessageDoc findAppointmentDetailMessage(Long appointmentDetailId) {
        Criteria appointmentDetailIdCriteria = Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_ID.getKey()).is(appointmentDetailId);
        return mongoTemplate.findOne(new Query(appointmentDetailIdCriteria), MessageDoc.class);
    }

    @Override
    public List<MessageGroupByAppointmentDetail> findDistinctAppointmentDetailMessage(List<Long> appointmentDetailIds) {
        Criteria appointmentDetailIdCriteria = Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_ID.getKey()).in(appointmentDetailIds);
        GroupOperation groupByStateAndSumPop = Aggregation.group(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_ID.getKey())
                .first(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_ID.getKey()).as(MessageDocEnum.APPOINTMENT_DETAIL_ID.getKey())
                .first(MessageDocEnum.CONVERSATION_ID.getKey()).as(MessageDocEnum.CONVERSATION_ID.getKey())
                .first(MessageDocEnum.MESSAGE_CENTER_ID.getKey()).as(MessageDocEnum.MESSAGE_CENTER_ID.getKey());

        ProjectionOperation projectionOperation = Aggregation.project(
                MessageDocEnum.APPOINTMENT_DETAIL_ID.getKey(),
                MessageDocEnum.CONVERSATION_ID.getKey(),
                MessageDocEnum.MESSAGE_CENTER_ID.getKey())
                .and(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_ID.getKey()).previousOperation();
        MatchOperation filterStates = match(appointmentDetailIdCriteria);

        Aggregation aggregation = newAggregation(filterStates, groupByStateAndSumPop, projectionOperation);
        AggregationResults<MessageGroupByAppointmentDetail> result = mongoTemplate.aggregate(
                aggregation, MessageDoc.class, MessageGroupByAppointmentDetail.class);

        return result.getMappedResults();
    }

    @Override
    public MessageDoc findByConversationIdAndTypes(ObjectId conversationId, List<Integer> messageTypes) {
        Criteria conversationCriteria = Criteria.where(MessageDocEnum.CONVERSATION_ID.getKey()).is(conversationId);
        Criteria appliedTypeCriteria = Criteria.where(MessageDocEnum.TYPE.getKey()).in(messageTypes);
        Criteria criteria = new Criteria().andOperator(conversationCriteria, appliedTypeCriteria, MessageDocEnum.IS_NOT_DELETED_CRITERIA);

        return mongoTemplate.findOne(new Query(criteria), MessageDoc.class);
    }

    @Override
    public MessageDoc findLastMessageBySenderIdAndBot(Long userId, Long botId) {
        Criteria senderIdCriteria = Criteria.where(MessageDocEnum.SENDER_USER_PROFILE_ID.getKey()).is(userId);
        Criteria botCriteria = Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).is(botId);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, MessageDocEnum.CREATED_DATE.getKey()));
        return mongoTemplate.findOne(new Query(new Criteria().andOperator(MessageDocEnum.IS_NOT_DELETED_CRITERIA, senderIdCriteria, botCriteria)).with(sort), MessageDoc.class);
    }

    @Override
    public void updateSender(ObjectId messageDocId, UserProfileCvEmbedded sender) {
        String senderKey = StringUtil.append(MessageDocEnum.SENDER.getKey(), ".");
        updateUserProfileCvEmbedded(messageDocId, senderKey, sender);
    }

    @Override
    public void updateRecipient(ObjectId messageDocId, UserProfileCvEmbedded recipient) {
        String recipientKey = StringUtil.append(MessageDocEnum.RECIPIENT.getKey(), ".");
        updateUserProfileCvEmbedded(messageDocId, recipientKey, recipient);
    }

    @Override
    public void updateApplicantAvailable(List<ObjectId> ids, boolean isAvailable) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(MessageDocEnum.ID.getKey()).in(ids),
                Criteria.where(MessageDocEnum.APPLIED_MESSAGE.getKey()).exists(true));

        Update update = new Update().set(MessageDocEnum.APPLIED_MESSAGE_IS_AVAILABLE.getKey(), isAvailable);
        mongoTemplate.updateMulti(new Query(criteria), update, MessageDoc.class);
    }

    @Override
    public void addAESSecretKeyForNoneSecretKeyMessageByConversationId(ObjectId id, String secretKey) {
        if (Objects.nonNull(id) && StringUtils.isNotBlank(secretKey)) {
            Criteria criteria = new Criteria().andOperator(
                    Criteria.where(MessageDocEnum.CONVERSATION_ID.getKey()).is(id),
                    Criteria.where(MessageDocEnum.SECRET_KEY.getKey()).exists(false));

            Update update = new Update().set(MessageDocEnum.SECRET_KEY.getKey(), secretKey);
            mongoTemplate.updateMulti(new Query(criteria), update, MessageDoc.class);
        }
    }

    @Override
    public List<MessageDoc> getSentMessagesByUser(long userId, List<Integer> receiveInApps) {
        if (CollectionUtils.isNotEmpty(receiveInApps)) {

            Criteria receiveInAppCriteria = Criteria.where(MessageDocEnum.RECEIVE_IN_APP.getKey()).in(receiveInApps);
            Criteria sentMessages = new Criteria().andOperator(
                    Criteria.where(MessageDocEnum.STATUS.getKey()).is(MessageConstants.MESSAGE_STATUS_SENT),
                    Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).is(userId),
                    recipientIsNotDeletedMessageCriteria, receiveInAppCriteria);

            Criteria appliedMessages = new Criteria().andOperator(
                    Criteria.where(MessageDocEnum.APPLIED_MESSAGE_STATUS.getKey()).is(MessageConstants.MESSAGE_STATUS_SENT),
                    Criteria.where(MessageDocEnum.SENDER_USER_PROFILE_ID.getKey()).is(userId),
                    senderIsNotDeletedMessageCriteria, receiveInAppCriteria);

            Criteria authorizationMessages = new Criteria().andOperator(
                    Criteria.where(MessageDocEnum.AUTHORIZATION_MESSAGE_STATUS.getKey()).is(MessageConstants.MESSAGE_STATUS_SENT),
                    Criteria.where(MessageDocEnum.SENDER_USER_PROFILE_ID.getKey()).is(userId),
                    senderIsNotDeletedMessageCriteria, receiveInAppCriteria);

            Criteria appointmentMessages = new Criteria().andOperator(
                    Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_STATUS.getKey()).is(MessageConstants.MESSAGE_STATUS_SENT),
                    Criteria.where(MessageDocEnum.SENDER_USER_PROFILE_ID.getKey()).is(userId),
                    senderIsNotDeletedMessageCriteria, receiveInAppCriteria);

            Criteria criteria = new Criteria().orOperator(sentMessages, authorizationMessages, appointmentMessages, appliedMessages);
            return mongoTemplate.find(new Query(criteria), MessageDoc.class);
        }
        return ImmutableList.of();
    }

    @Override
    public void updateDateTimeRangeAndType(long appointmentDetailId, List<Date> dateRanges, List<Date> timeRanges, int type, Date fromDate, Date toDate) {
        Criteria criteria = Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_ID.getKey()).is(appointmentDetailId);

        Update update = new Update();
        update.set(MessageDocEnum.APPOINTMENT_DATE_RANGE.getKey(), dateRanges);
        update.set(MessageDocEnum.APPOINTMENT_TIME_RANGE.getKey(), timeRanges);
        update.set(MessageDocEnum.APPOINTMENT_TYPE.getKey(), type);
        update.set(MessageDocEnum.APPOINTMENT_FROM_DATE.getKey(), fromDate);
        update.set(MessageDocEnum.APPOINTMENT_TO_DATE.getKey(), toDate);
        mongoTemplate.updateMulti(new Query(criteria), update, MessageDoc.class);
    }

    @Override
    public void deleteMessages(ObjectId messageCenterId, Long userProfileId, int receiveInApp) {
        Criteria messageCenterCriteria = Criteria.where(MessageDocEnum.MESSAGE_CENTER_ID.getKey()).is(messageCenterId);
        Criteria senderCriteria = Criteria.where(MessageDocEnum.SENDER_USER_PROFILE_ID.getKey()).is(userProfileId);
        Criteria recipientCriteria = Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).is(userProfileId);
        Criteria receiveInAppCriteria = Criteria.where(MessageDocEnum.RECEIVE_IN_APP.getKey()).is(receiveInApp);

        Criteria criteria = new Criteria().andOperator(messageCenterCriteria, senderCriteria, receiveInAppCriteria, MessageDocEnum.IS_NOT_DELETED_CRITERIA);
        Update senderUpdate = new Update().set(MessageDocEnum.SENDER_IS_DELETED_MESSAGE.getKey(), true);
        mongoTemplate.updateMulti(new Query(criteria), senderUpdate, MessageDoc.class);

        criteria = new Criteria().andOperator(messageCenterCriteria, recipientCriteria, receiveInAppCriteria, MessageDocEnum.IS_NOT_DELETED_CRITERIA);
        Update recipientUpdate = new Update().set(MessageDocEnum.RECIPIENT_IS_DELETED_MESSAGE.getKey(), true);
        mongoTemplate.updateMulti(new Query(criteria), recipientUpdate, MessageDoc.class);
    }

    @Override
    public long countAppointmentMessage(ObjectId conversationId, int responseStatus) {
        Criteria appointmentMessage = new Criteria();
        appointmentMessage.andOperator(
                Criteria.where(MessageDocEnum.CONVERSATION_ID.getKey()).is(conversationId),
                Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.APPOINTMENT_MESSAGE),
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_RESPONSE_STATUS.getKey()).is(responseStatus),
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_DETAIL_STATUS.getKey()).nin(AppointmentStatus.getCancelStatus()),
                Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_TO_DATE.getKey()).gte(DateUtils.toServerTimeForMongo()),
                MessageDocEnum.IS_NOT_DELETED_CRITERIA);
        return mongoTemplate.count(new Query(appointmentMessage), MessageDoc.class);
    }

    @Override
    public boolean addAESSecretKeyForNoneSecretKeyMessageByConversationIds(List<ConversationDoc> conversationDocs) {
        return super.addAESSecretKeyForNoneSecretKeyMessageByConversationIds(conversationDocs);
    }

    @Override
    public void syncIsDeletedMessageInMessageDoc() {
        String senderKey = StringUtil.append(MessageDocEnum.SENDER.getKey(), Constants.DOT, UserProfileCvEmbeddedEnum.IS_DELETED_MESSAGE.getKey());
        Criteria senderCriteria = Criteria.where(senderKey).exists(false);
        Update updateSender = new Update().set(senderKey, false);
        mongoTemplate.updateMulti(new Query(senderCriteria), updateSender, MessageDoc.class);

        String recipientKey = StringUtil.append(MessageDocEnum.RECIPIENT.getKey(), Constants.DOT, UserProfileCvEmbeddedEnum.IS_DELETED_MESSAGE.getKey());
        Criteria recipientCriteria = Criteria.where(recipientKey).exists(false);
        Update updateRecipient = new Update().set(recipientKey, false);
        mongoTemplate.updateMulti(new Query(recipientCriteria), updateRecipient, MessageDoc.class);
    }

    @Override
    public List<MessageDoc> findBoostHelperMessageBySenderAndReceiverAndEventType(Long senderId, Long recipientId, int boostHelperEventType) {
        Criteria criteria = getCriteriaToFindBoostHelperMgs(senderId, recipientId, boostHelperEventType);
        return mongoTemplate.find(new Query(criteria), MessageDoc.class);
    }

    @Override
    public List<MessageDoc> findBoostHelperMessageBySenderAndReceiverAndEventType(long senderId, Long recipientId, int boostHelperEventType, Long userQualificationId) {
        Criteria assessmentCriteria = Criteria.where(MessageDocEnum.BOOST_HELPER_QUALIFICATION_ID.getKey()).is(userQualificationId);
        Criteria criteria = new Criteria().andOperator(getCriteriaToFindBoostHelperMgs(senderId, recipientId, boostHelperEventType), assessmentCriteria);
        return mongoTemplate.find(new Query(criteria), MessageDoc.class);
    }

    private Criteria getCriteriaToFindBoostHelperMgs(Long senderId, Long recipientId, int boostHelperEventType) {
        Criteria senderCriteria = Criteria.where(MessageDocEnum.SENDER_USER_PROFILE_ID.getKey()).is(senderId);
        Criteria receiverCriteria = Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).is(recipientId);
        Criteria boostHelperEventTypeCriteria = Criteria.where(MessageDocEnum.BOOST_HELPER_EVENT_TYPE.getKey()).is(boostHelperEventType);
        return new Criteria().andOperator(MessageDocEnum.IS_NOT_DELETED_CRITERIA, senderCriteria, receiverCriteria, boostHelperEventTypeCriteria);
    }

    @Override
    public boolean hasApplicantMessage(ObjectId messageCenterId, long recipientId) {
        Criteria receiverCriteria = Criteria.where(MessageDocEnum.RECIPIENT_USER_PROFILE_ID.getKey()).is(recipientId);
        Criteria messageTypeCriteria = new Criteria().orOperator(
                Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.APPLICANT_MESSAGE),
                Criteria.where(MessageDocEnum.TYPE.getKey()).is(MessageConstants.APPOINTMENT_APPLICANT_MESSAGE));

        Criteria messageCenterCriteria = Criteria.where(MessageDocEnum.MESSAGE_CENTER_ID.getKey()).is(messageCenterId);
        Criteria criteria = new Criteria().andOperator(Criteria.where(MessageDocEnum.IS_DELETED.getKey()).is(false),
                receiverCriteria, messageTypeCriteria, messageCenterCriteria);
        return mongoTemplate.exists(new Query(criteria), MessageDoc.class);
    }

    private void updateUserProfileCvEmbedded(ObjectId messageDocId, String userProfileKey, UserProfileCvEmbedded userProfileCvEmbedded) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(MessageDocEnum.ID.getKey()).is(messageDocId),
                Criteria.where(StringUtil.append(userProfileKey, UserProfileEmbeddedEnum.USER_PROFILE_ID.getKey())).is(userProfileCvEmbedded.getUserProfileId()),
                Criteria.where(StringUtil.append(userProfileKey, UserProfileEmbeddedEnum.USER_TYPE.getKey())).is(userProfileCvEmbedded.getUserType())
        );

        Update updater = initUserProfileCvEmbedded(userProfileKey, userProfileCvEmbedded);
        mongoTemplate.updateMulti(new Query(criteria), updater, MessageDoc.class);
    }

    private Update initUserProfileCvEmbedded(String prefix, UserProfileCvEmbedded cvEmbedded) {
        Update updater = new Update();
        Map<String, Object> map = MongoInitData.initUserProfileCvEmbedded(prefix, cvEmbedded);
        map.forEach(updater::set);
        return updater;
    }
}