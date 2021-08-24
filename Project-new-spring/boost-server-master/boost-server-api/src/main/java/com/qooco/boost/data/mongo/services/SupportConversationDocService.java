package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.entities.SupportConversationDoc;
import com.qooco.boost.data.mongo.services.base.ConversationBaseService;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface SupportConversationDocService extends ConversationBaseService<SupportConversationDoc, ObjectId> {
    SupportConversationDoc save(MessageCenterDoc messageCenter, UserProfileCvEmbedded sender, UserProfileCvEmbedded recipient, int fromApp);

    SupportConversationDoc findBySenderAndFromApp(long userProfileId, int userType, int fromApp);

    Page<SupportConversationDoc> findByAppAndStatusAndLanguageSortBy(Integer appId, Integer status, int page, int size, String... sortBy);

    void updateStatus(String id, int status);
}