package com.qooco.boost.data.mongo.services.impl;

import com.qooco.boost.data.enumeration.doc.UserCvDocEnum;
import com.qooco.boost.data.model.UserCvDocAggregation;
import com.qooco.boost.data.mongo.embedded.CurrencyEmbedded;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.repositories.UserCvDocRepository;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.impl.abstracts.UserCVDocAbstract;
import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.utils.MatchingCandidateConfig;
import com.qooco.boost.threads.models.DataFeedback;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.qooco.boost.data.mongo.services.impl.Boot2MongoUtils.newQueryObject;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class UserCvDocServiceImpl extends UserCVDocAbstract implements UserCvDocService {

    @Autowired
    private UserCvDocRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public MongoRepository getRepository() {
        return repository;
    }

    @Override
    public UserCvDoc findByUserProfileId(long userProfileId) {
        Criteria criteria = Criteria.where(UserCvDocEnum.USER_PROFILE_ID.key()).is(userProfileId);
        return mongoTemplate.findOne(new Query(criteria), UserCvDoc.class);
    }

    @Override
    public List<UserCvDoc> findByProvince(Long provinceId) {
        Criteria provinceCriteria = Criteria.where(UserCvDocEnum.USER_PROFILE_CITY_PROVINCE_ID.key()).is(provinceId);
        return mongoTemplate.find(new Query(provinceCriteria), UserCvDoc.class);
    }

    @Override
    public List<UserCvDoc> findByProvinceAndProfileStrength(Long provinceId, int profileStrength) {
        Criteria provinceCriteria = Criteria.where(UserCvDocEnum.USER_PROFILE_CITY_PROVINCE_ID.key()).is(provinceId);
        Criteria profileStrengthCriteria = Criteria.where(UserCvDocEnum.PROFILE_STRENGTH.key()).gte(profileStrength);
        return mongoTemplate.find(new Query(new Criteria().andOperator(provinceCriteria, profileStrengthCriteria)), UserCvDoc.class);
    }

    @Override
    public List<UserCvDoc> findByUserProfileId(List<Long> userProfileIds) {
        Criteria criteria = Criteria.where(UserCvDocEnum.USER_PROFILE_ID.key()).in(userProfileIds);
        return mongoTemplate.find(new Query(criteria), UserCvDoc.class);
    }

    @Override
    public Page<UserCvDoc> findMatchingCvForVacancy(VacancyDoc vacancyDoc, int page, int size, int limitTime, MatchingCandidateConfig config) {
        Query query = initMatchingCvForVacancy(vacancyDoc, limitTime, config);

        Sort sort = Sort.by(new Order(Direction.ASC, UserCvDoc.Fields.minSalary),
                new Order(Direction.DESC, UserCvDocEnum.EDUCATION_ID.key()),
                new Order(Direction.DESC, UserCvDoc.Fields.isAsap),
                new Order(Direction.ASC, UserCvDoc.Fields.expectedStartDate));
        query.with(sort).limit(size).skip(page * size);
        List<UserCvDoc> userCvs = mongoTemplate.find(query, UserCvDoc.class);

        return new PageImpl<>(userCvs);
    }

    @Override
    public List<UserCvDocAggregation> findMatchingCvForVacancyWithPreferredHotelSorting(VacancyDoc vacancyDoc, int page, int size, int limitTime, MatchingCandidateConfig config) {
        var criteria = initCriteriaMatchingCvForVacancy(vacancyDoc, limitTime, config);
        var matchStage = Aggregation.match(criteria);

        var preferredHotelOperation = initOperation()
                .and(aggregationOperationContext -> newQueryObject("$setIsSubset", List.of(
                        List.of(vacancyDoc.getCompany().getId()),
                        "$preferredHotels._id"))).as("isPreferredHotel");

        var sort = Sort.by(
                new Order(Direction.DESC, "isPreferredHotel"),
                new Order(Direction.ASC, UserCvDoc.Fields.minSalary),
                new Order(Direction.DESC, UserCvDocEnum.EDUCATION_ID.key()),
                new Order(Direction.DESC, UserCvDoc.Fields.isAsap),
                new Order(Direction.ASC, UserCvDoc.Fields.expectedStartDate));
        var sortOperation = sort(sort);
        var operations = Arrays.asList(matchStage, preferredHotelOperation, sortOperation);
        return getUserCvAggregationResult(page * size, size, operations);
    }

    @Override
    public List<UserCvDocAggregation> findMatchingCvForVacancySortByExperience(VacancyDoc vacancyDoc,
                                                                               int offset,
                                                                               int size,
                                                                               int limitTime,
                                                                               MatchingCandidateConfig config) {
        Criteria criteria = initCriteriaMatchingCvForVacancy(vacancyDoc, limitTime, config);
        MatchOperation matchStage = Aggregation.match(criteria);
        Sort sort = Sort.by(
                new Order(Direction.DESC, UserCvDocAggregation.Fields.dateDifference),
                new Order(Direction.DESC, UserCvDocEnum.USER_PROFILE_ID.key()));
        SortOperation sortOperation = sort(sort);

        Date now = new Date();
        ProjectionOperation projectToMatchModel = initOperation().andInclude("startWorking", "endWorking")
                .and("minStartDate").applyCondition(ConditionalOperators.ifNull("startWorking").then(now))
                .and("maxEndDate").applyCondition(ConditionalOperators.ifNull("endWorking").then(now));
        ProjectionOperation projectToDateDiff = initOperation().andInclude("maxEndDate", "minStartDate")
                .andExpression("maxEndDate - minStartDate").as("dateDifference");
        List<AggregationOperation> operations = Arrays.asList(matchStage, projectToMatchModel, projectToDateDiff, sortOperation);

        return getUserCvAggregationResult(offset, size, operations);
    }

    @Override
    public List<UserCvDocAggregation> findMatchingCvForVacancySortByAssessment(VacancyDoc vacancyDoc, long assessmentId, int offset, int size, int limitTime, MatchingCandidateConfig config) {
        Criteria criteria = initCriteriaMatchingCvForVacancy(vacancyDoc, limitTime, config);
        MatchOperation matchStage = Aggregation.match(criteria);
        Sort sort = Sort.by(new Order(Direction.DESC, "assessmentLevel"),
                new Order(Direction.DESC, UserCvDocEnum.USER_PROFILE_ID.key()));
        SortOperation sortOperation = sort(sort);

        Date expiredDate = DateUtils.addDays(DateUtils.toServerTimeForMongo(), -config.getExpiredDays());

        Map<String, Object> filterExpression = newQueryObject("input", "$qualifications");
        filterExpression.put("as", "qualification");

        filterExpression.put("cond", newQueryObject("$and", Arrays.<Object>asList(
                newQueryObject("$eq", Arrays.<Object>asList("$$qualification.assessment._id", assessmentId)),
                newQueryObject("$gte", Arrays.<Object>asList("$$qualification.submissionTime", expiredDate)))));
        ProjectionOperation qualificationOperator = initOperation()
                .and(aggregationOperationContext -> newQueryObject("$filter", filterExpression)).as("qualificationEmbeddedList");
        ProjectionOperation assessmentLevelOperation = initOperation().andInclude("qualificationEmbeddedList")
                .and(aggregationOperationContext -> newQueryObject("$max", "$qualificationEmbeddedList.level.assessmentLevel")).as("assessmentLevel");
        List<AggregationOperation> operations = Arrays.asList(matchStage, qualificationOperator, assessmentLevelOperation, sortOperation);

        return getUserCvAggregationResult(offset, size, operations);
    }

    private List<UserCvDocAggregation> getUserCvAggregationResult(int offset, int size, List<AggregationOperation> aggregationOperations) {
        LimitOperation limitOperation = limit(size);
        SkipOperation skipOperation = skip(Long.valueOf(offset));
        Aggregation aggregation = Aggregation.newAggregation(
                Stream.concat(aggregationOperations.stream(),
                        Stream.of(skipOperation, limitOperation)).collect(toList()))
                .withOptions(newAggregationOptions().allowDiskUse(true).build());

        return mongoTemplate.aggregate(aggregation, UserCvDoc.class, UserCvDocAggregation.class).getMappedResults();
    }

    private ProjectionOperation initOperation() {
        return project()
                .andInclude("isHourSalary",
                        "minSalary",
                        "maxSalary",
                        "isAsap",
                        "expectedStartDate",
                        "isFullTime",
                        "socialLinks",
                        "currency",
                        "education",
                        "desiredHours",
                        "benefits",
                        "jobs",
                        "preferredHotels",
                        "previousPositions",
                        "softSkills",
                        "userProfile",
                        "profileStrength",
                        "updatedDate",
                        "qualifications",
                        "hasPersonality",
                        "id");
    }

    @Override
    public Page<UserCvDoc> findMatchingCvForVacancySortBySalary(VacancyDoc vacancyDoc, int offset, int size, int limitTime, MatchingCandidateConfig config) {
        Query query = initMatchingCvForVacancy(vacancyDoc, limitTime, config);
        Sort sort = Sort.by(new Order(Direction.ASC, UserCvDoc.Fields.maxSalary),
                new Order(Direction.ASC, UserCvDoc.Fields.minSalary),
                new Order(Direction.DESC, UserCvDocEnum.USER_PROFILE_ID.key()));
        query.with(sort).limit(size).skip(offset);
        List<UserCvDoc> userCvs = mongoTemplate.find(query, UserCvDoc.class);
        return new PageImpl<>(userCvs);
    }


    @Override
    public UserCvDoc findMaxSalaryMatchingCvForVacancy(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        Query query = initMatchingCvForVacancy(vacancyDoc, limitTime, config);
        Sort sort = Sort.by(new Order(Direction.DESC, UserCvDoc.Fields.maxSalary));
        query.with(sort).limit(1);
        return mongoTemplate.findOne(query, UserCvDoc.class);
    }

    @Override
    public long countMatchingCvForVacancy(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        Query query = initMatchingCvForVacancy(vacancyDoc, limitTime, config);
        return mongoTemplate.count(query, UserCvDoc.class);
    }



    @Override
    public long countMatchingCvForVacancyIgnoreSalary(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        Query query = new Query(initCriteriaIgnoreSalary(vacancyDoc, limitTime, config));
        return mongoTemplate.count(query, UserCvDoc.class);
    }


    @Override
    public List<UserCvDoc> getUserCvNotSyncSuccessful() {
        Criteria usernameCriteria = Criteria.where(UserCvDocEnum.USERNAME.key()).exists(false);
        Criteria scaleIdCriteria = new Criteria().andOperator(
                Criteria.where(UserCvDoc.Fields.qualifications).exists(true),
                Criteria.where(UserCvDocEnum.QUALIFICATIONS_ASSESSMENT_SCALE_ID.key()).exists(false));
        Criteria criteria = new Criteria().orOperator(usernameCriteria, scaleIdCriteria);
        return mongoTemplate.find(new Query(criteria), UserCvDoc.class);
    }

    @Override
    public List<UserCvDoc> getUserCvNotSyncWorkingDateExperience(int limit, List<Long> exceptedUserIds) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where(UserCvDoc.Fields.startWorking).exists(false),
                Criteria.where(UserCvDoc.Fields.endWorking).exists(false));
        if (CollectionUtils.isNotEmpty(exceptedUserIds)) {
            criteria = new Criteria().andOperator(criteria, Criteria.where(UserCvDoc.Fields.id).nin(exceptedUserIds));
        }
        Query query = new Query(criteria).limit(limit);
        return mongoTemplate.find(query, UserCvDoc.class);
    }

    @Override
    public List<UserCvDoc> getByIdGreaterThan(Long prevId, int limit) {
        Criteria criteria = Criteria.where(UserCvDoc.Fields.id).gt(prevId);
        Sort sort = Sort.by(new Order(Direction.ASC, UserCvDoc.Fields.id));
        Query query = new Query(criteria).limit(limit).with(sort);
        return mongoTemplate.find(query, UserCvDoc.class);
    }

    @Override
    public List<UserCvDoc> findOpenVacancyByCurrencyIdGreaterThan(long currencyId, Long prevId, int limit) {
        Criteria criteria = new Criteria().andOperator(Criteria.where(UserCvDoc.Fields.id).gt(prevId),
                Criteria.where(UserCvDocEnum.CURRENCY_ID.key()).is(currencyId));
        Sort sort = Sort.by(new Order(Direction.ASC, UserCvDoc.Fields.id));
        Query query = new Query(criteria).limit(limit).with(sort);
        return mongoTemplate.find(query, UserCvDoc.class);
    }

    @Override
    public List<Long> findIdByIds(List<Long> ids) {
        Criteria criteria = Criteria.where(UserCvDoc.Fields.id).in(ids);
        Sort sort = Sort.by(new Order(Direction.ASC, UserCvDoc.Fields.id));
        Query query = new Query(criteria).with(sort);
        query.fields().include(UserCvDoc.Fields.id);
        List<UserCvDoc> userCvDocs = mongoTemplate.find(query, UserCvDoc.class);
        return userCvDocs.stream().map(UserCvDoc::getId).collect(toImmutableList());
    }

    private Query initMatchingCvForVacancy(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        Query query = new Query();
        ofNullable(initCriteriaMatchingCvForVacancy(vacancyDoc, limitTime, config)).ifPresent(query::addCriteria);
        return query;
    }



    @Override
    public void addDefaultValueForHasPersonality() {
        Criteria hasPersonality = Criteria.where(UserCvDoc.Fields.hasPersonality).exists(false);
        Update update = new Update().set(UserCvDoc.Fields.hasPersonality, false);
        mongoTemplate.updateMulti(new Query(hasPersonality), update, UserCvDoc.class);
    }

    @Override
    public void updateHasPersonality(long id, boolean hasPersonality) {
        Criteria personalityCriteria = Criteria.where(UserCvDoc.Fields.id).is(id);
        Update update = new Update().set(UserCvDoc.Fields.hasPersonality, hasPersonality);
        mongoTemplate.updateFirst(new Query(personalityCriteria), update, UserCvDoc.class);
    }

    @Override
    public boolean isSalaryTooHigh(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        long countCandidate = countMatchingCvForVacancy(vacancyDoc, limitTime, config);
        double newMaxSalary = vacancyDoc.getSalaryMaxUsd() * DataFeedback.HIGHER_RATE;
        vacancyDoc.setSalaryMaxUsd(newMaxSalary);
        long countCandidateAfterAdjustment = countMatchingCvForVacancy(vacancyDoc, limitTime, config);
        return (countCandidateAfterAdjustment >= DataFeedback.MIN_SIMILAR_PROFILE && (countCandidateAfterAdjustment * DataFeedback.ONE_VALUE / countCandidate) > DataFeedback.MIN_SIMILAR_PROFILE_RATE);
    }

    @Override
    public void updateCurrencyAndSalaryInUsd(Currency currency, List<UserCvDoc> userCvDocs) {
        if (CollectionUtils.isNotEmpty(userCvDocs) && Objects.nonNull(currency)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, UserCvDoc.class);
            CurrencyEmbedded currencyEmbedded = MongoConverters.convertToCurrencyEmbedded(currency);
            userCvDocs.forEach(userCvDoc -> {
                Criteria vacancyIdCriteria = Criteria.where(UserCvDoc.Fields.id).is(userCvDoc.getId());
                Update update = new Update();

                double usdRate = userCvDoc.getCurrency().getValidUnitPerUsd();
                double salaryUsd = userCvDoc.getMinSalary() / usdRate;
                double salaryMaxUsd = userCvDoc.getMaxSalary() / usdRate;
                update.set(UserCvDoc.Fields.minSalaryUsd, salaryUsd);
                update.set(UserCvDoc.Fields.maxSalaryUsd, salaryMaxUsd);

                update.set(UserCvDoc.Fields.currency, currencyEmbedded);

                bulkOps.updateMulti(new Query(vacancyIdCriteria), update);
            });
            try {
                bulkOps.execute();
            } catch (IllegalArgumentException ignore) {
            }
        }
    }

    @Override
    public void addDefaultArrayForPreferredHotels() {
        var hasPreferredHotels = Criteria.where(UserCvDoc.Fields.preferredHotels).exists(false);
        var update = new Update().set(UserCvDoc.Fields.preferredHotels, List.of());
        mongoTemplate.updateMulti(new Query(hasPreferredHotels), update, UserCvDoc.class);
    }

    @Override
    public boolean isSalaryTooLow(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        long countCandidate = countMatchingCvForVacancy(vacancyDoc, limitTime, config);
        double newMinSalary = vacancyDoc.getSalaryUsd() * DataFeedback.LOWER_RATE;
        vacancyDoc.setSalaryUsd(newMinSalary);
        vacancyDoc.setSalaryMaxUsd(vacancyDoc.getCurrency().getMaxSalary() * vacancyDoc.getCurrency().getValidUsdPerUnit());
        long countCandidateAfterAdjustment = countMatchingCvForVacancy(vacancyDoc, limitTime, config);
        return (countCandidateAfterAdjustment >= DataFeedback.MIN_SIMILAR_PROFILE && (countCandidateAfterAdjustment * DataFeedback.ONE_VALUE / countCandidate) > DataFeedback.MIN_SIMILAR_PROFILE_RATE );
    }






}
