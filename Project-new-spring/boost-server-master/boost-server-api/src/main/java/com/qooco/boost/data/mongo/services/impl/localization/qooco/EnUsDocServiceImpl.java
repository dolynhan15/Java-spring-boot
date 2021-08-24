package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.EnUsDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.EnUsDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.EnUsDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnUsDocServiceImpl implements EnUsDocService {

    @Autowired
    private EnUsDocRepository enUsDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<EnUsDoc> save(List<EnUsDoc> enUsDocs) {
        return enUsDocRepository.saveAll(enUsDocs);
    }

    @Override
    public EnUsDoc save(EnUsDoc enUsDoc) {
        return enUsDocRepository.save(enUsDoc);
    }

    @Override
    public EnUsDoc findById(String id) {
        return enUsDocRepository.findById(id).orElse(null);
    }

    @Override
    public EnUsDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, EnUsDoc.class);
    }

    @Override
    public List<EnUsDoc> findByIds(List<String> id) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.ID).in(id);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.find(query, EnUsDoc.class);
    }

}
