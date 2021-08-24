package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.entities.OwnedPackageDoc;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/16/2018 - 3:03 PM
 */
public interface OwnedPackageDocService {
    OwnedPackageDoc save(OwnedPackageDoc ownedPackageDoc);

    OwnedPackageDoc findById(String id);

    List<OwnedPackageDoc> save(List<OwnedPackageDoc> ownedPackageDocs);

    OwnedPackageDoc findLatestOwnedPackage(Long userProfileId);

    List<OwnedPackageDoc>  findByUserProfile(Long userProfileId);

    List<OwnedPackageDoc> findByPackageId(List<Long> packageId);

    List<OwnedPackageDoc> findAll();
}
