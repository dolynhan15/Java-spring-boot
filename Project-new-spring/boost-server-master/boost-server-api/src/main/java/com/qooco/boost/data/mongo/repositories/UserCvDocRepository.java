package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.UserCvDoc;
import org.springframework.stereotype.Repository;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 2:35 PM
*/
@Repository
public interface UserCvDocRepository extends Boot2MongoRepository<UserCvDoc, Long> {

//    @Query(value = "[ {'$match': ?0 }," +
//            " {$project:{ \n" +
//            "        \"maxEndDate\": {$ifNull:[{$max: \"$previousPositions.endDate\"}, new Date()]} ,\n" +
//            "        \"minStartDate\": {$ifNull:[{$min: \"$previousPositions.startDate\"}, new Date()]},\n" +
//            "        \n" +
//            "        }\n" +
//            "        }," +
//            "{$project: {\n" +
//            "            \"maxEndDate\": 1,\n" +
//            "            \"minStartDate\": 1,\n" +
//            "            \"dateDifference\": {$subtract: [\"$maxEndDate\", \"$minStartDate\"]}\n" +
//            "            }\n" +
//            "        }]")
//    Page<UserCvDocAggregation> findByCreatedById(String userId, Pageable pageable);
}
