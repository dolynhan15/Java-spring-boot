package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.SupportMessageDoc;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportMessageDocRepository extends Boot2MongoRepository<SupportMessageDoc, ObjectId> {
}
