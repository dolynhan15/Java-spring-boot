package com.qooco.boost.data.mongo.services.impl.localization.qooco;

import com.qooco.boost.data.constants.LocalizationDocConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.MsMyDoc;
import com.qooco.boost.data.mongo.repositories.localization.qooco.MsMyDocRepository;
import com.qooco.boost.data.mongo.services.localization.qooco.MsMyDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MsMyDocServiceImpl implements MsMyDocService {

    @Autowired
    private MsMyDocRepository msMyDocRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public MsMyDoc save(MsMyDoc msMyDoc) {
        return msMyDocRepository.save(msMyDoc);
    }

    @Override
    public MsMyDoc findById(String id) {
        return msMyDocRepository.findById(id).orElse(null);
    }

    @Override
    public MsMyDoc findByLatestCollection(String collection) {
        Criteria criteria = Criteria.where(LocalizationDocConstants.COLLECTION).is(collection);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, LocalizationDocConstants.TIMESTAMP));
        Query query = new Query(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, MsMyDoc.class);
    }

    @Override
    public List<MsMyDoc> save(List<MsMyDoc> msMyDocs) {
        return msMyDocRepository.saveAll(msMyDocs);
    }

}
