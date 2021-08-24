package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.model.MessageGroupByAppointmentDetail;
import com.qooco.boost.data.model.count.LongCount;
import com.qooco.boost.data.model.count.ObjectIdCount;
import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.base.MessageBaseService;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface MessageDocService extends MessageBaseService<MessageDoc> {
    MessageDoc save(MessageDoc messageDoc, boolean isEditUpdatedDate);

    List<MessageDoc> save(List<MessageDoc> messageDocs);

    MessageDoc findById(ObjectId id);

    MessageDoc findAppointmentDetailMessage(Long appointmentDetailId);

    MessageDoc findByRecipientAndStaffAndType(Long recipientId, StaffEmbedded staffEmbedded, int type);

    MessageDoc findByConversationIdAndTypes(ObjectId conversationId, List<Integer> messageTypes);

    MessageDoc findLastMessageBySenderIdAndBot(Long userId, Long botId);

    List<MessageDoc> findByIds(List<ObjectId> ids);

    List<MessageDoc> findByAppointmentDetailId(Long appointmentDetailId);

    List<MessageDoc> findByJoinCompanyRequestId(long joinCompanyRequestId);

    List<MessageDoc> findFirstMessage(List<ObjectId> conversationIds);

    List<MessageDoc> findApplicantMessageBySenderAndTypeAndVacancy(Long userProfileId, List<Long> vacancyIds, List<Integer> responseStatus);

    List<MessageDoc> findInterestedApplicantMessage(VacancyDoc vacancyDoc);

    List<MessageDoc> findInterestedApplicantOrApplicationAppointmentMessage(VacancyDoc vacancyDoc);

    long countAppointmentMessage(ObjectId conversationId, int status);

    List<LongCount> countUnreadMessageByUserProfileCvId(List<Long> userProfileId, int receiveInApp, long senderId);

    List<ObjectIdCount> countMessageGroupByConversation(List<Long> appointmentDetailIds);

    List<Long> getVacancyHaveAppointmentDetailByRecipient(Long userProfileId, List<Long> vacancyIds, List<Integer> status);

    List<ObjectId> findMessageCenterHavingClosedMessage(List<ObjectId> messageCenterIds, Long userProfileId);

    List<MessageGroupByAppointmentDetail> findDistinctAppointmentDetailMessage(List<Long> appointmentDetailIds);

    void updateSeenForOlderMessage(MessageDoc messageDoc, boolean isUpdateAppointmentResponseStatus);

    void updateReceivedForOlderMessage(MessageDoc messageDoc, boolean isUpdateAppointmentResponseStatus);

    void softDeleteMessageByConversationIds(List<ObjectId> conversationIds);

    void updateSender(ObjectId messageDocId, UserProfileCvEmbedded sender);

    void updateRecipient(ObjectId messageDocId, UserProfileCvEmbedded recipient);

    void updateApplicantAvailable(List<ObjectId> ids, boolean isAvailable);

    void addAESSecretKeyForNoneSecretKeyMessageByConversationId(ObjectId id, String secretKey);

    void updateDateTimeRangeAndType(long id, List<Date> dateRanges, List<Date> timeRanges, int type, Date fromDate, Date toDate);

    void deleteMessages(ObjectId messageCenterId, Long userProfileId, int receiveInApp);

    void syncIsDeletedMessageInMessageDoc();

    List<MessageDoc> findBoostHelperMessageBySenderAndReceiverAndEventType(Long senderId, Long recipientId, int boostHelperEventType);

    List<MessageDoc> findBoostHelperMessageBySenderAndReceiverAndEventType(long senderId, Long recipientId, int boostHelperEventType, Long userQualificationId);

    boolean hasApplicantMessage(ObjectId messageCenterId, long recipientId);
}
