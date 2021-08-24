package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.RuRuDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.RuRuDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.RuRuDocService;
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
public class RuRuDocServiceImpl implements RuRuDocService {

    @Autowired
    private RuRuDocRepository ruRuDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RuRuDoc save(RuRuDoc ruRuDoc) {
        return ruRuDocRepository.save(ruRuDoc);
    }

    @Override
    public RuRuDoc findById(String id) {
        return ruRuDocRepository.findById(id).orElse(null);
    }

    @Override
    public RuRuDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, RuRuDoc.class);
    }

    @Override
    public List<RuRuDoc> save(List<RuRuDoc> ruRuDocs) {
        return ruRuDocRepository.saveAll(ruRuDocs);
    }

}
