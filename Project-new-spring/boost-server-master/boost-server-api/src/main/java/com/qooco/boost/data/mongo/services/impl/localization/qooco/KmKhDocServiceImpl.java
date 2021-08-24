package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.KmKhDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.KmKhDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.KmKhDocService;
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
public class KmKhDocServiceImpl implements KmKhDocService {

    @Autowired
    private KmKhDocRepository kmKhDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public KmKhDoc save(KmKhDoc kmKhDoc) {
        return kmKhDocRepository.save(kmKhDoc);
    }

    @Override
    public KmKhDoc findById(String id) {
        return kmKhDocRepository.findById(id).orElse(null);
    }

    @Override
    public KmKhDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, KmKhDoc.class);
    }

    @Override
    public List<KmKhDoc> save(List<KmKhDoc> kmKhDocs) {
        return kmKhDocRepository.saveAll(kmKhDocs);
    }

}
