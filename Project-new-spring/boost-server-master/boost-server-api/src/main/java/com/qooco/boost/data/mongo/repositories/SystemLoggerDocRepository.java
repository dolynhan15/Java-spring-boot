package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.SystemLoggerDoc;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemLoggerDocRepository extends Boot2MongoRepository<SystemLoggerDoc, ObjectId> {
}
