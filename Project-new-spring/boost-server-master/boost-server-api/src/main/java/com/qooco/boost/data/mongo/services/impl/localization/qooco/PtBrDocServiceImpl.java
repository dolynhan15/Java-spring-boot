package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.PtBrDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.PtBrDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.PtBrDocService;
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
public class PtBrDocServiceImpl implements PtBrDocService {

    @Autowired
    private PtBrDocRepository ptBrDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public PtBrDoc save(PtBrDoc ptBrDoc) {
        return ptBrDocRepository.save(ptBrDoc);
    }

    @Override
    public PtBrDoc findById(String id) {
        return ptBrDocRepository.findById(id).orElse(null);
    }

    @Override
    public PtBrDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, PtBrDoc.class);
    }

    @Override
    public List<PtBrDoc> save(List<PtBrDoc> ptBrDocs) {
        return ptBrDocRepository.saveAll(ptBrDocs);
    }

}
