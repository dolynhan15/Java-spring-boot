package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.MyMmDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.MyMmDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.MyMmDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyMmDocServiceImpl implements MyMmDocService {

    @Autowired
    private MyMmDocRepository myMmDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public MyMmDoc save(MyMmDoc myMmDoc) {
        return myMmDocRepository.save(myMmDoc);
    }

    @Override
    public MyMmDoc findById(String id) {
        return myMmDocRepository.findById(id).orElse(null);
    }

    @Override
    public MyMmDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, MyMmDoc.class);
    }

    @Override
    public List<MyMmDoc> save(List<MyMmDoc> myMmDocs) {
        return myMmDocRepository.saveAll(myMmDocs);
    }

}
