package com.qooco.boost.data.mongo.repositories;

import com.qooco.boost.data.mongo.entities.OwnedPackageDoc;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/16/2018 - 3:18 PM
 */
@Repository
public interface OwnedPackageDocRepository extends Boot2MongoRepository<OwnedPackageDoc, String> {
    List<OwnedPackageDoc> findByUserProfileId(@Param("userProfileId")Long userProfileId);
}
