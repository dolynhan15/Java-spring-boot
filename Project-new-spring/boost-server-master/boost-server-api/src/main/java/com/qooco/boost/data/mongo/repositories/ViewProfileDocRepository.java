package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.ViewProfileDoc;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewProfileDocRepository extends Boot2MongoRepository<ViewProfileDoc, ObjectId> {
}
