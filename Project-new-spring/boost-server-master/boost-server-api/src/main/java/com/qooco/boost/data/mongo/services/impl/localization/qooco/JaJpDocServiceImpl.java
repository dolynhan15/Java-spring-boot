package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.JaJpDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.JaJpDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.JaJpDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JaJpDocServiceImpl implements JaJpDocService {

    @Autowired
    private JaJpDocRepository jaJpDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public JaJpDoc save(JaJpDoc jaJpDoc) {
        return jaJpDocRepository.save(jaJpDoc);
    }

    @Override
    public JaJpDoc findById(String id) {
        return jaJpDocRepository.findById(id).orElse(null);
    }

    @Override
    public JaJpDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, JaJpDoc.class);
    }

    @Override
    public List<JaJpDoc> save(List<JaJpDoc> jaJpDocs) {
        return jaJpDocRepository.saveAll(jaJpDocs);
    }

}
