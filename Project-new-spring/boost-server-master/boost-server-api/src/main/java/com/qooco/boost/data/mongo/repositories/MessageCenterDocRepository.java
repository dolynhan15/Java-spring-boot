package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 2:35 PM
*/
@Repository
public interface MessageCenterDocRepository extends Boot2MongoRepository<MessageCenterDoc, ObjectId> {
}
