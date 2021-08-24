package com.qooco.boost.data.mongo.repositories.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.MsMyDoc;
import com.qooco.boost.data.mongo.repositories.Boot2MongoRepository;
import org.springframework.stereotype.Repository;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/2/2018 - 9:48 AM
*/
@Repository
public interface MsMyDocRepository extends Boot2MongoRepository<MsMyDoc, String> {
}
