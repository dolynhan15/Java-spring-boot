package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.VacancyDoc;
import org.springframework.stereotype.Repository;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 2:35 PM
*/
@Repository
public interface VacancyDocRepository extends Boot2MongoRepository<VacancyDoc, Long> {
}
