package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.KoKrDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.KoKrDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.KoKrDocService;
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
public class KoKrDocServiceImpl implements KoKrDocService {

    @Autowired
    private KoKrDocRepository koKrDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public KoKrDoc save(KoKrDoc koKrDoc) {
        return koKrDocRepository.save(koKrDoc);
    }

    @Override
    public KoKrDoc findById(String id) {
        return koKrDocRepository.findById(id).orElse(null);
    }

    @Override
    public KoKrDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, KoKrDoc.class);
    }

    @Override
    public List<KoKrDoc> save(List<KoKrDoc> koKrDocs) {
        return koKrDocRepository.saveAll(koKrDocs);
    }

}
