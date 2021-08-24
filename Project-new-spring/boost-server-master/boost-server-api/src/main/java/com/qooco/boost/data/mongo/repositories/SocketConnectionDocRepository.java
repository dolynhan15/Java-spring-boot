package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.SocketConnectionDoc;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public interface SocketConnectionDocRepository extends Boot2MongoRepository<SocketConnectionDoc, ObjectId> {
}
