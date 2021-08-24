package com.qooco.boost.data.mongo.services.base;

import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import com.qooco.boost.data.mongo.services.DocService;
import com.qooco.boost.models.sdo.ConversationPublicKeySDO;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface ConversationBaseService<T extends ConversationBase, K extends Serializable> extends DocService<T, K> {
    long countByMessageCenterIdAndUserProfileIdWithTimestamp(ObjectId messageCenterId, Long userProfileId, Long timestamp);

    List<T> findByMessageCenterIdAndUserProfileIdWithTimestamp(ObjectId messageCenterId, Long userProfileId, Long timestamp, int size);

    List<T> findByMessageCenterIdAndUserProfileId(List<ObjectId> messageCenterIds, Long userProfile, List<String> includeFields);

    List<T> findNotHavePublicKeyByToken(Long userId, String accessToken, String publicKey, int limit);

    T findByIdAndUserProfileId(ObjectId id, Long userProfileId);

    void updateNowForUpdatedDate(List<ObjectId> ids, Date date);

    boolean updateBatchSecretKeyForConversations(List<ConversationDoc> conversationDocs);

    boolean updateBatchEncryptionKeyForConversationDocs(List<ConversationPublicKeySDO> conversationPublicKeySDOS);

    int removeAccessTokenInUserKey(String accessToken);
}
