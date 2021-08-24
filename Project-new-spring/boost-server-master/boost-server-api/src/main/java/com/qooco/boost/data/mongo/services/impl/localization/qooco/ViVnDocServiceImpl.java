package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.ViVnDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.ViVnDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.ViVnDocService;
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
public class ViVnDocServiceImpl implements ViVnDocService {

    @Autowired
    private ViVnDocRepository viVnDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ViVnDoc save(ViVnDoc viVnDoc) {
        return viVnDocRepository.save(viVnDoc);
    }

    @Override
    public ViVnDoc findById(String id) {
        return viVnDocRepository.findById(id).orElse(null);
    }

    @Override
    public ViVnDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, ViVnDoc.class);
    }

    @Override
    public List<ViVnDoc> save(List<ViVnDoc> viVnDocs) {
        return viVnDocRepository.saveAll(viVnDocs);
    }

}
