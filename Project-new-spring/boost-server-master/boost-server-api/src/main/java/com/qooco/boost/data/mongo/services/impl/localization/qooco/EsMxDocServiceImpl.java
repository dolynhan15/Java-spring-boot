package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.EsMxDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.EsMxDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.EsMxDocService;
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
public class EsMxDocServiceImpl implements EsMxDocService {

    @Autowired
    private EsMxDocRepository esMxDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public EsMxDoc save(EsMxDoc esMxDoc) {
        return esMxDocRepository.save(esMxDoc);
    }

    @Override
    public EsMxDoc findById(String id) {
        return esMxDocRepository.findById(id).orElse(null);
    }

    @Override
    public EsMxDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, EsMxDoc.class);
    }

    @Override
    public List<EsMxDoc> save(List<EsMxDoc> esMxDocs) {
        return esMxDocRepository.saveAll(esMxDocs);
    }

}
