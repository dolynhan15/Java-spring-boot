package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

/*
* Copyright: Falcon Team - AxonActive
 User: tnlong
 Date: 7/26/2018 - 2:35 PM
*/
@Repository
public interface AssessmentTestHistoryDocRepository extends Boot2MongoRepository<AssessmentTestHistoryDoc, ObjectId> {
}
