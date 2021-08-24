package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.MessageDoc;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 2:35 PM
*/
@Repository
public interface MessageDocRepository extends Boot2MongoRepository<MessageDoc, ObjectId> {
}
