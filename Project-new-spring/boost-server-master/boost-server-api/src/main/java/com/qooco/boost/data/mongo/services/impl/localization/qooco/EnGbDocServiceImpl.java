package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.EnGbDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.EnGbDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.EnGbDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/2/2018 - 10:59 AM
*/
@Service
public class EnGbDocServiceImpl implements EnGbDocService {

    @Autowired
    private EnGbDocRepository enGbDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public EnGbDoc save(EnGbDoc enGbDoc) {
        return enGbDocRepository.save(enGbDoc);
    }

    @Override
    public EnGbDoc findById(String id) {
        return enGbDocRepository.findById(id).orElse(null);
    }

    @Override
    public EnGbDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        return mongoTemplate.findOne(new Query(criteria).with(sort), EnGbDoc.class);
    }

    @Override
    public List<EnGbDoc> save(List<EnGbDoc> enGbDocs) {
        return enGbDocRepository.saveAll(enGbDocs);
    }

    @Override
    public List<EnGbDoc> findByIds(List<String> id) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.ID).in(id);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        return mongoTemplate.find(new Query(criteria).with(sort), EnGbDoc.class);
    }
}
