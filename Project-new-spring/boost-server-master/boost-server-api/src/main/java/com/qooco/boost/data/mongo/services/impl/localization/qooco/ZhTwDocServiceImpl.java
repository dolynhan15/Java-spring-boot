package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.ZhTwDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.ZhTwDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.ZhTwDocService;
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
 Date: 10/2/2018 - 11:30 AM
*/
@Service
public class ZhTwDocServiceImpl implements ZhTwDocService {

    @Autowired
    private ZhTwDocRepository zhTwDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ZhTwDoc save(ZhTwDoc zhTwDoc) {
        return zhTwDocRepository.save(zhTwDoc);
    }

    @Override
    public ZhTwDoc findById(String id) {
        return zhTwDocRepository.findById(id).orElse(null);
    }

    @Override
    public ZhTwDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, ZhTwDoc.class);
    }

    @Override
    public List<ZhTwDoc> save(List<ZhTwDoc> zhTwDocs) {
        return zhTwDocRepository.saveAll(zhTwDocs);
    }
}
