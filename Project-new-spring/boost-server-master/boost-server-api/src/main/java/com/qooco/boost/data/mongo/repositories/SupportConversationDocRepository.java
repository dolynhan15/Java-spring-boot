package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.SupportConversationDoc;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportConversationDocRepository extends Boot2MongoRepository<SupportConversationDoc, ObjectId>  {
}
