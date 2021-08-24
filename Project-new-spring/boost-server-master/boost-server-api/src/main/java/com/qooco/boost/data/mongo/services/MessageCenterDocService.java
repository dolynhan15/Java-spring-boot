package com.qooco.boost.data.mongo.services;

import com.mongodb.WriteResult;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MessageCenterDocService extends DocService<MessageCenterDoc, ObjectId>{
    MessageCenterDoc findByVacancy(Long vacancyId);

    MessageCenterDoc findMessageCenterForAuthorizationByCompany(long companyId);

    MessageCenterDoc findByTypeAndUserOfBoostHelper(int messageType, Long userId, int userType);

    List<ObjectId> findByVacancy(List<Long> vacancyId);

    List<MessageCenterDoc> findByUserProfileFromHotel(long userProfileId, List<ObjectId> ids);

    List<MessageCenterDoc> findByTypeAndUserProfile(List<Integer> types, long userProfileId, List<ObjectId> ids);

    List<MessageCenterDoc> findByTypeAndUserProfileAndTimestamp(List<Integer> types, long userProfileId, Long timestamp, int size);

    List<MessageCenterDoc> findMessageCenterForVacancyByContactUserProfileAndCompany(long userProfileId, long companyId);

    List<MessageCenterDoc> findByUserProfileFromHotelAndTimestamp(long userProfileId, Long timestamp, int size);

    long countByTypeAndUserProfileAndTimestamp(List<Integer> types, long userProfileId, Long timestamp);

    long countByUserProfileFromHotelAndTimestamp(long userProfileId, Long timestamp);

    void updateNowForUpdatedDate(List<ObjectId> ids, Date date);

    void updateFreeChatParticipant(ObjectId id, UserProfileCvEmbedded recruiter, UserProfileCvEmbedded candidate);

    void updateAdminsOfCompany(Long companyId, UserProfileCvEmbedded admin, String oldRoleName, String newRoleName);

    void deleteMessageCenterOfUser(ObjectId messageCenterId, Long userProfileId, boolean isHotelApp);

    void syncIsDeletedMessageInMessageCenterDoc();

    WriteResult updateNumberOfCandidate(MessageCenterDoc messageCenterDoc);
    //TODO: Will remove in next sprint after release production

    @Deprecated
    void updateAdminsOfCompany(Map<Long, List<UserProfileCvEmbedded>> adminOfCompanyMap);
}
