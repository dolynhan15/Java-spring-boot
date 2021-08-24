package com.qooco.boost.data.mongo.services.impl;

import com.qooco.boost.data.enumeration.SearchCriteria;
import com.qooco.boost.data.enumeration.doc.UserCvDocEnum;
import com.qooco.boost.data.model.QualificationAverage;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.UserFeedbackDataService;
import com.qooco.boost.data.mongo.services.impl.abstracts.UserCVDocAbstract;
import com.qooco.boost.data.utils.MatchingCandidateConfig;
import com.qooco.boost.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.util.Optional.ofNullable;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;

@Service
public class UserFeedbackDataServiceImpl extends UserCVDocAbstract implements UserFeedbackDataService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<UserCvDoc> findMatchingCvForVacancyNotEnoughExperience(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config, List<Long> excludeUserCvIds) {
        //Inform for 6 months
        Date lastEndWorking = DateUtils.addDays(new Date(), -6 * 30);
        Criteria criteria = initCriteriaMatchingCvForVacancy(vacancyDoc, limitTime, config);
        criteria = new Criteria().andOperator(
                criteria,
                Criteria.where(UserCvDoc.Fields.id).nin(excludeUserCvIds),
                new Criteria().orOperator(
                        Criteria.where(UserCvDoc.Fields.startWorking).exists(false),
                        Criteria.where(UserCvDoc.Fields.endWorking).lte(lastEndWorking)
                )
        );
        return mongoTemplate.find(new Query(criteria), UserCvDoc.class);
    }

    @Override
    public List<UserCvDoc> findMatchingCvForVacancySalaryTooHigh(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        Criteria criteria = initCriteriaIgnoreSalary(vacancyDoc, limitTime, config);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, UserCvDoc.Fields.maxSalary));
        var query = new Query(criteria).with(sort);
        query.fields().include(UserCvDoc.Fields.id).include(UserCvDoc.Fields.maxSalaryUsd);
        return mongoTemplate.find(query, UserCvDoc.class);
    }

    @Override
    public List<UserCvDoc> findMatchingCvForVacancyNoQualification(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        var criteria = initCriteriaIgnoreQualification(vacancyDoc, limitTime, config);
        var query = new Query(criteria);
        query.fields().include(UserCvDoc.Fields.id).include(UserCvDoc.Fields.qualifications);
        return mongoTemplate.find(query, UserCvDoc.class);
    }

    @Override
    public long countMatchingCvForVacancyIgnoreQualification(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        Query query = new Query(initCriteriaIgnoreQualification(vacancyDoc, limitTime, config));
        return mongoTemplate.count(query, UserCvDoc.class);
    }

    @Override
    public List<QualificationAverage> getAverageLevelByVacancy(VacancyDoc vacancyDoc, int limitTime,
                                                               MatchingCandidateConfig config) {
        Criteria criteria = initCriteriaIgnoreQualification(vacancyDoc, limitTime, config);

        MatchOperation matchOperation = Aggregation.match(criteria);
        UnwindOperation unwindOperation = Aggregation.unwind(UserCvDoc.Fields.qualifications);
        List<Criteria> qualificationCriterias = new ArrayList<>();
        Date expiredDate = DateUtils.addDays(DateUtils.toServerTimeForMongo(), -config.getExpiredDays());
        vacancyDoc.getQualifications().forEach(qualification -> {
            Criteria qualificationCriteria = new Criteria().andOperator(
                    Criteria.where(UserCvDocEnum.QUALIFICATIONS_ASSESSMENT_ID.key()).is(qualification.getAssessment().getId()),
                    Criteria.where(UserCvDocEnum.QUALIFICATIONS_SUBMISSION_TIME.key()).gte(expiredDate));
            qualificationCriterias.add(qualificationCriteria);

        });
        MatchOperation qualificationMatchOperation = new MatchOperation(new Criteria().orOperator(qualificationCriterias.toArray(new Criteria[0])));
        GroupOperation groupOperation = Aggregation.group(UserCvDocEnum.QUALIFICATIONS_ASSESSMENT_ID.key())
                .avg(UserCvDocEnum.QUALIFICATIONS_LEVEL_ASSESSMENT_LEVEL.key()).as(QualificationAverage.Fields.avgLevel);
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                unwindOperation,
                qualificationMatchOperation,
                groupOperation)
                .withOptions(newAggregationOptions().allowDiskUse(true).build());

        return mongoTemplate.aggregate(aggregation, UserCvDoc.class, QualificationAverage.class).getMappedResults();
    }
    private Criteria initCriteriaIgnoreQualification(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        int alphaTestId = Objects.nonNull(config) ? config.getVacancyJobAlphaTest() : -1;
        if (alphaTestId != vacancyDoc.getJob().getId()) {
            var criteria = new ArrayList<Criteria>();
            SearchCriteria.getVacancyMatchingCriteriaIgnoreQualification().forEach(it -> ofNullable(initSearchCriteria(it, vacancyDoc, config, limitTime)).ifPresent(criteria::add));
            return new Criteria().andOperator(criteria.toArray(new Criteria[0]));
        } else {
            return initAlphaTestCriteria(vacancyDoc, limitTime, config);
        }
    }
}
