package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.PushNotificationDoc;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public interface PushNotificationDocRepository extends Boot2MongoRepository<PushNotificationDoc, ObjectId> {
}
