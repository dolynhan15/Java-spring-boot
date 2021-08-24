package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.DeDeDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.DeDeDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.DeDeDocService;
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
public class DeDeDocServiceImpl implements DeDeDocService {

    @Autowired
    private DeDeDocRepository deDeDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public DeDeDoc save(DeDeDoc deDeDoc) {
        return deDeDocRepository.save(deDeDoc);
    }

    @Override
    public DeDeDoc findById(String id) {
        return deDeDocRepository.findById(id).orElse(null);
    }

    @Override
    public DeDeDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, DeDeDoc.class);
    }

    @Override
    public List<DeDeDoc> save(List<DeDeDoc> deDeDocs) {
        return deDeDocRepository.saveAll(deDeDocs);
    }

}
