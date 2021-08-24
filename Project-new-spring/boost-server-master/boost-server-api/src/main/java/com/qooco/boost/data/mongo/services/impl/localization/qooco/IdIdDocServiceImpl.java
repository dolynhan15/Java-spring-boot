package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.IdIdDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.IdIdDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.IdIdDocService;
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
public class IdIdDocServiceImpl implements IdIdDocService {

    @Autowired
    private IdIdDocRepository idIdDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public IdIdDoc save(IdIdDoc idIdDoc) {
        return idIdDocRepository.save(idIdDoc);
    }

    @Override
    public IdIdDoc findById(String id) {
        return idIdDocRepository.findById(id).orElse(null);
    }

    @Override
    public IdIdDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, IdIdDoc.class);
    }

    @Override
    public List<IdIdDoc> save(List<IdIdDoc> idIdDocs) {
        return idIdDocRepository.saveAll(idIdDocs);
    }

}
