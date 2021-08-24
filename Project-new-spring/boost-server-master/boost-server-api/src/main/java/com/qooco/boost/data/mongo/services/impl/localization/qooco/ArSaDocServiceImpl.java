package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.ArSaDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.ArSaDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.ArSaDocService;
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
public class ArSaDocServiceImpl implements ArSaDocService {

    @Autowired
    private ArSaDocRepository arSaDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ArSaDoc save(ArSaDoc arSaDoc) {
        return arSaDocRepository.save(arSaDoc);
    }

    @Override
    public ArSaDoc findById(String id) {
        return arSaDocRepository.findById(id).orElse(null);
    }

    @Override
    public ArSaDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, ArSaDoc.class);
    }

    @Override
    public List<ArSaDoc> save(List<ArSaDoc> arSaDocs) {
        return arSaDocRepository.saveAll(arSaDocs);
    }

}
