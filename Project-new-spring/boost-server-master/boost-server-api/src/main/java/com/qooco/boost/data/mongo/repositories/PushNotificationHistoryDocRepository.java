package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.PushNotificationHistoryDoc;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public interface PushNotificationHistoryDocRepository extends Boot2MongoRepository<PushNotificationHistoryDoc, ObjectId> {
}
