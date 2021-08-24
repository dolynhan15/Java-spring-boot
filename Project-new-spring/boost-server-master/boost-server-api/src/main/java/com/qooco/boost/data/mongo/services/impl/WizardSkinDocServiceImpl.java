package com.qooco.boost.data.mongo.services.impl;

import com.qooco.boost.data.constants.WizardSkinDocConstants;
import com.qooco.boost.data.mongo.entities.WizardSkinDoc;
import com.qooco.boost.data.mongo.repositories.WizardSkinRepository;
import com.qooco.boost.data.mongo.services.WizardSkinDocService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/9/2018 - 10:44 AM
*/
@Service
public class WizardSkinDocServiceImpl implements WizardSkinDocService {

    @Autowired
    private WizardSkinRepository wizardSkinRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public WizardSkinDoc save(WizardSkinDoc wizardSkinDoc) {
        return wizardSkinRepository.save(wizardSkinDoc);
    }

    @Override
    public WizardSkinDoc findById(Long id) {
        return wizardSkinRepository.findById(id).orElse(null);
    }

    @Override
    public List<WizardSkinDoc> save(List<WizardSkinDoc> wizardSkinDocs) {
        return wizardSkinRepository.saveAll(wizardSkinDocs);
    }

    @Override
    public List<WizardSkinDoc> findByTestIds(List<Long> testId) {
        if (CollectionUtils.isNotEmpty(testId)) {
            Criteria criteria = Criteria.where(WizardSkinDocConstants.QUESTION_ANSWERS_TEST_ID).in(testId);
            Query query = new Query(criteria);
            Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, WizardSkinDocConstants.TIMESTAMP));
            query.with(sort);
            return mongoTemplate.find(query, WizardSkinDoc.class);
        }
        return new ArrayList<>();
    }

    @Override
    public WizardSkinDoc findByLatestWizardSkin() {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, WizardSkinDocConstants.TIMESTAMP));
        Query query = new Query();
        query.with(sort);
        return mongoTemplate.findOne(query, WizardSkinDoc.class);
    }
}
