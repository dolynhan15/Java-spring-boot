package com.qooco.boost.data.mongo.services.impl;

import com.qooco.boost.data.constants.LevelTestScaleDocConstants;
import com.qooco.boost.data.mongo.entities.LevelTestScaleDoc;
import com.qooco.boost.data.mongo.repositories.LevelTestScaleDocRepository;
import com.qooco.boost.data.mongo.services.LevelTestScaleDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LevelTestScaleDocServiceImpl implements LevelTestScaleDocService {

    @Autowired
    private LevelTestScaleDocRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public MongoRepository getRepository() {
        return repository;
    }

    @Override
    public LevelTestScaleDoc findByLatestLevelTestScale() {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LevelTestScaleDocConstants.TIMESTAMP));
        return mongoTemplate.findOne(new Query().with(sort), LevelTestScaleDoc.class);
    }

    @Override
    public List<LevelTestScaleDoc> findByScaleIds(List<String> scaleIds) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LevelTestScaleDocConstants.TIMESTAMP));
        Criteria criteria = Criteria.where(LevelTestScaleDocConstants.ID).in(scaleIds);
        return mongoTemplate.find(new Query(criteria).with(sort), LevelTestScaleDoc.class);
    }

    @Override
    public List<LevelTestScaleDoc> findAll() {
        return repository.findAll();
    }
}
