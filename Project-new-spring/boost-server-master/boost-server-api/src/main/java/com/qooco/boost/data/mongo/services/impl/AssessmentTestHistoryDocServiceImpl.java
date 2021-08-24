package com.qooco.boost.data.mongo.services.impl;

import com.qooco.boost.data.constants.AssessmentTestHistoryDocConstants;
import com.qooco.boost.data.enumeration.doc.AssessmentTestHistoryDocEnum;
import com.qooco.boost.data.model.AssessmentLatest;
import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;
import com.qooco.boost.data.mongo.repositories.AssessmentTestHistoryDocRepository;
import com.qooco.boost.data.mongo.services.AssessmentTestHistoryDocService;
import com.qooco.boost.data.utils.IdGeneration;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
public class AssessmentTestHistoryDocServiceImpl implements AssessmentTestHistoryDocService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AssessmentTestHistoryDocRepository repository;

    @Override
    public MongoRepository getRepository() {
        return repository;
    }

    @Override
    public List<AssessmentTestHistoryDoc> getTestHistoryByAssessment(Long userProfileId, Long assessmentId) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, AssessmentTestHistoryDocEnum.SUBMISSION_TIME.getKey()));

        Criteria criteriaUser = Criteria.where(AssessmentTestHistoryDocEnum.USER_PROFILE_ID.getKey()).is(userProfileId);
        Criteria criteriaAssessment = Criteria.where(AssessmentTestHistoryDocEnum.ASSESSMENT_ID.getKey()).is(assessmentId);
        Criteria criteriaLevel = Criteria.where(AssessmentTestHistoryDocEnum.LEVEL_ASSESSMENT_LEVEL.getKey()).gt(0);

        Criteria criteria = new Criteria().andOperator(criteriaUser, criteriaAssessment, criteriaLevel);
        return mongoTemplate.find(new Query().addCriteria(criteria).with(sort), AssessmentTestHistoryDoc.class);
    }

    @Override
    public AssessmentTestHistoryDoc findLatestSyncLevelTestData() {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, AssessmentTestHistoryDocConstants.UPDATED_DATE));
        return mongoTemplate.findOne(new Query().with(sort), AssessmentTestHistoryDoc.class);
    }

    @Override
    public AssessmentTestHistoryDoc findByLatestLevelTestHistoryByUser(Long userProfileId) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, AssessmentTestHistoryDocConstants.UPDATED_DATE_BY_IT_SELF));
        Criteria criteria = Criteria.where(AssessmentTestHistoryDocConstants.USER_PROFILE_ID).is(userProfileId);
        Query query = new Query().addCriteria(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, AssessmentTestHistoryDoc.class);
    }

    @Override
    public List<AssessmentTestHistoryDoc> findByUserAndAssessmentIds(Long userProfileId, List<Long> assessmentIds) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, AssessmentTestHistoryDoc.Fields.submissionTime));
        Criteria userCriteria = Criteria.where(AssessmentTestHistoryDoc.Fields.userProfileId).is(userProfileId);
        Criteria assessmentCriteria = Criteria.where(AssessmentTestHistoryDoc.Fields.assessmentId).in(assessmentIds);
        Query query = new Query().addCriteria(new Criteria().andOperator(userCriteria, assessmentCriteria)).with(sort);
        return mongoTemplate.find(query, AssessmentTestHistoryDoc.class);
    }

    @Override
    public int countByUserProfileIdAndScaleIdAndAssessmentId(Long userProfileId, String scaleId, Long assessmentId, boolean isValidQualification) {
        Criteria criteria1Level = isValidQualification ?
                Criteria.where(AssessmentTestHistoryDocEnum.LEVEL_ASSESSMENT_LEVEL.getKey()).gt(0) :
                Criteria.where(AssessmentTestHistoryDocEnum.LEVEL_ASSESSMENT_LEVEL.getKey()).gte(0);


        Criteria criteria = new Criteria().andOperator(Criteria.where(AssessmentTestHistoryDocEnum.USER_PROFILE_ID.getKey()).is(userProfileId),
                Criteria.where(AssessmentTestHistoryDocEnum.SCALE_ID.getKey()).is(scaleId),
                Criteria.where(AssessmentTestHistoryDocEnum.ASSESSMENT_ID.getKey()).is(assessmentId),
                criteria1Level);
        return (int) mongoTemplate.count(new Query().addCriteria(criteria), AssessmentTestHistoryDoc.class);
    }

    @Override
    public List<AssessmentTestHistoryDoc> getLastTestAllAssessment(Long userProfileId) {

        Criteria userCriteria = new Criteria().andOperator(
                Criteria.where(AssessmentTestHistoryDocEnum.USER_PROFILE_ID.getKey()).is(userProfileId),
                Criteria.where(AssessmentTestHistoryDocEnum.LEVEL_ASSESSMENT_LEVEL.getKey()).gt(0));

        Aggregation aggregation = newAggregation(
                Aggregation.match(userCriteria),
                Aggregation.group(AssessmentTestHistoryDocEnum.ASSESSMENT_ID.getKey())
                        .last(AssessmentTestHistoryDocEnum.SUBMISSION_TIME.getKey())
                        .as(AssessmentTestHistoryDocEnum.SUBMISSION_TIME.getKey()),
                Aggregation.sort(Sort.Direction.DESC, AssessmentTestHistoryDocEnum.SUBMISSION_TIME.getKey()));

        AggregationResults<AssessmentLatest> groupResults
                = mongoTemplate.aggregate(aggregation, AssessmentTestHistoryDoc.class, AssessmentLatest.class);

        List<AssessmentLatest> result = groupResults.getMappedResults();
        if (CollectionUtils.isNotEmpty(result)) {
            List<String> ids = result.stream()
                    .map(asl -> IdGeneration.generateTestHistory(userProfileId, asl.getId(), asl.getSubmissionTime().getTime()))
                    .collect(Collectors.toList());
            Criteria idsCriteria = Criteria.where(AssessmentTestHistoryDocConstants.ID).in(ids);
            Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, AssessmentTestHistoryDocConstants.UPDATED_DATE));
            Query query = new Query().addCriteria(idsCriteria);
            query.with(sort);
            return mongoTemplate.find(query, AssessmentTestHistoryDoc.class);
        }
        return new ArrayList<>();
    }
}
