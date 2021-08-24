package com.qooco.boost.data.mongo.services.impl;

import com.qooco.boost.data.enumeration.doc.PushNotificationDocEnum;
import com.qooco.boost.data.mongo.entities.PushNotificationDoc;
import com.qooco.boost.data.mongo.repositories.PushNotificationDocRepository;
import com.qooco.boost.data.mongo.services.PushNotificationDocService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PushNotificationDocServiceImpl implements PushNotificationDocService {
    @Autowired
    private PushNotificationDocRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public PushNotificationDoc save(PushNotificationDoc doc) {
        return repository.save(doc);
    }

    @Override
    public PushNotificationDoc findById(ObjectId id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public PushNotificationDoc findByRequestId(Long requestId) {
        Criteria criteria = Criteria.where(PushNotificationDocEnum.REQUEST_ID.getKey()).is(requestId);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, PushNotificationDoc.class);
    }

    @Override
    public void deleteById(ObjectId id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteByRequestId(Long requestId) {
        Criteria criteria = Criteria.where(PushNotificationDocEnum.REQUEST_ID.getKey()).is(requestId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, PushNotificationDoc.class);
    }

    @Override
    public List<PushNotificationDoc> findByRequest(int[] errorCodes) {
        Criteria criteria = Criteria.where(PushNotificationDocEnum.ERROR_CODE.getKey()).in(errorCodes);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, PushNotificationDoc.class);
    }
}
