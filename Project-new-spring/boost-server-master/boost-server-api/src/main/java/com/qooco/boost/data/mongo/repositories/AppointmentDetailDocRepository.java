package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentDetailDocRepository extends Boot2MongoRepository<AppointmentDetailDoc, Long> {
}
