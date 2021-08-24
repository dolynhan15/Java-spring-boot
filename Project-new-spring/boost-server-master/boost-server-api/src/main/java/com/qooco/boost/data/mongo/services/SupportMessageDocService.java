package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.entities.SupportMessageDoc;
import com.qooco.boost.data.mongo.services.base.MessageBaseService;
import org.bson.types.ObjectId;

public interface SupportMessageDocService extends MessageBaseService<SupportMessageDoc> {
    SupportMessageDoc save(SupportMessageDoc messageDoc, boolean isEditUpdatedDate);

    SupportMessageDoc findById(ObjectId id);

    void updateSeenForOlderMessage(SupportMessageDoc message);

    void updateReceivedForOlderMessage(SupportMessageDoc message);
}
