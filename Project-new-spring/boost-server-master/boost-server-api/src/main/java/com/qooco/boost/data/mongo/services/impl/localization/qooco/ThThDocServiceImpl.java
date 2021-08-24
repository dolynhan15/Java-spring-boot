package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.ThThDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.ThThDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.ThThDocService;
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
public class ThThDocServiceImpl implements ThThDocService {

    @Autowired
    private ThThDocRepository thThDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ThThDoc save(ThThDoc thThDoc) {
        return thThDocRepository.save(thThDoc);
    }

    @Override
    public ThThDoc findById(String id) {
        return thThDocRepository.findById(id).orElse(null);
    }

    @Override
    public ThThDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, ThThDoc.class);
    }

    @Override
    public List<ThThDoc> save(List<ThThDoc> thThDocs) {
        return thThDocs;
    }

}
