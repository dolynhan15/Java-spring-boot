package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.entities.PushNotificationDoc;
import org.bson.types.ObjectId;

import java.util.List;

public interface PushNotificationDocService {
    PushNotificationDoc save(PushNotificationDoc doc);

    PushNotificationDoc findById(ObjectId id);

    PushNotificationDoc findByRequestId(Long requestId);

    List<PushNotificationDoc> findByRequest(int[] errorCodes);

    void deleteById(ObjectId id);

    void deleteByRequestId(Long requestId);

}
