package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.ZhCnDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.ZhCnDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.ZhCnDocService;
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
 Date: 10/2/2018 - 11:29 AM
*/
@Service
public class ZhCnDocServiceImpl implements ZhCnDocService {
    @Autowired
    private ZhCnDocRepository zhCnDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ZhCnDoc save(ZhCnDoc zhCnDoc) {
        return zhCnDocRepository.save(zhCnDoc);
    }

    @Override
    public ZhCnDoc findById(String id) {
        return zhCnDocRepository.findById(id).orElse(null);
    }

    @Override
    public ZhCnDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, ZhCnDoc.class);
    }

    @Override
    public List<ZhCnDoc> save(List<ZhCnDoc> zhCnDocs) {
        return zhCnDocRepository.saveAll(zhCnDocs);
    }
}
