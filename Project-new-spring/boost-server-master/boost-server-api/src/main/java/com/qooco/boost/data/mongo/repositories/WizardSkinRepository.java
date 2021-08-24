package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.WizardSkinDoc;
import org.springframework.stereotype.Repository;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/9/2018 - 10:44 AM
*/
@Repository
public interface WizardSkinRepository extends Boot2MongoRepository<WizardSkinDoc, Long> {
}
