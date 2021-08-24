package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.ConversationDoc;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationDocRepository extends Boot2MongoRepository<ConversationDoc, ObjectId> {
}
