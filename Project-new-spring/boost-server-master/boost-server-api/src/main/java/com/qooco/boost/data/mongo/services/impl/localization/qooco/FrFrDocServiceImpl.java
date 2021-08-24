package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.FrFrDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.FrFrDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.FrFrDocService;
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
public class FrFrDocServiceImpl implements FrFrDocService {

    @Autowired
    private FrFrDocRepository frFrDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public FrFrDoc save(FrFrDoc frFrDoc) {
        return frFrDocRepository.save(frFrDoc);
    }

    @Override
    public FrFrDoc findById(String id) {
        return frFrDocRepository.findById(id).orElse(null);
    }

    @Override
    public FrFrDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, FrFrDoc.class);
    }

    @Override
    public List<FrFrDoc> save(List<FrFrDoc> frFrDocs) {
        return frFrDocRepository.saveAll(frFrDocs);
    }

}
