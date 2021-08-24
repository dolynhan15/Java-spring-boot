package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.CompanyDoc;
import org.springframework.stereotype.Repository;
@Repository
public interface CompanyDocRepository extends Boot2MongoRepository<CompanyDoc, Long> {
}
