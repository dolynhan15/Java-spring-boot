package com.qooco.boost.data.mongo.services.impl;

import com.qooco.boost.data.constants.OwnedPackageConstants;
import com.qooco.boost.data.mongo.entities.OwnedPackageDoc;
import com.qooco.boost.data.mongo.repositories.OwnedPackageDocRepository;
import com.qooco.boost.data.mongo.services.OwnedPackageDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/16/2018 - 3:18 PM
 */
@Service
public class OwnedPackageDocServiceImpl implements OwnedPackageDocService {
    @Autowired
    private OwnedPackageDocRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public OwnedPackageDoc save(OwnedPackageDoc ownedPackageDoc) {
        return repository.save(ownedPackageDoc);
    }


    @Override
    public List<OwnedPackageDoc> save(List<OwnedPackageDoc> ownedPackageDocs) {
        return repository.saveAll(ownedPackageDocs);
    }

    @Override
    public OwnedPackageDoc findById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public OwnedPackageDoc findLatestOwnedPackage(Long userProfileId) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, OwnedPackageConstants.TIMESTAMP));
        Criteria criteria = Criteria.where(OwnedPackageConstants.USER_PROFILE_ID).is(userProfileId);
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, OwnedPackageDoc.class);
    }

    @Override
    public List<OwnedPackageDoc> findByPackageId(List<Long> packageId) {
        return null;
    }

    @Override
    public List<OwnedPackageDoc> findAll() {
        return null;
    }

    @Override
    public List<OwnedPackageDoc> findByUserProfile(Long userProfileId) {
        return repository.findByUserProfileId(userProfileId);
    }
}
