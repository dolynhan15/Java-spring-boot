package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessMatchingCandidateService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.Const;
import com.qooco.boost.data.model.UserCvDocAggregation;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.utils.MatchingCandidateConfig;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.user.UserCurriculumVitaeDTO;
import com.qooco.boost.models.response.CandidatesResp;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusinessMatchingCandidateServiceImpl implements BusinessMatchingCandidateService {

    @Autowired
    private UserCvDocService userCvDocService;
    @Autowired
    private VacancyDocService vacancyDocService;
    @Autowired
    private BoostActorManager boostActorManager;
    @Autowired
    private BusinessValidatorService businessValidatorService;

    //Service link from other system
    @Value(ApplicationConstant.BOOST_PATA_VACANCY_JOB_ALPHA_TESTER)
    private int vacancyJobAlphaTest;

    //Service link from other system
    @Value(ApplicationConstant.MATCHING_CANDIDATE_BOOST_SCORE)
    private boolean boostScoreEnabled;

    @Value(ApplicationConstant.BOOST_PATA_VACANCY_REJECTED_LIMIT_TIME)
    private int rejectedLimitTime;

    @Value(ApplicationConstant.BOOST_PATA_CERTIFICATION_PERIOD)
    private int expiredDays;

    @Override
    public BaseResp findMatchingCvForVacancy(long userProfileId, long vacancyId, int size, Authentication authentication) {
        VacancyDoc vacancyDoc = validateMatchingRequest(userProfileId, vacancyId, size);

        MatchingCandidateConfig config = initMatchingCandidateConfig();
        var result = userCvDocService.findMatchingCvForVacancyWithPreferredHotelSorting(vacancyDoc, 0, size, rejectedLimitTime, config);
        boostActorManager.updateMessageCenterDocInMongoActor(vacancyDoc);
        List<UserCurriculumVitaeDTO> userCvs = getUserCvDTO(result, getLocale(authentication));
        return new BaseResp<>(userCvs);
    }

    @Override
    public BaseResp findMatchingCvForVacancyWithComparison(long userProfileId, long vacancyId, int size, int offset, int sortType, Authentication authentication) {
        if (sortType != 1 && sortType != 2) {
            throw new InvalidParamException(ResponseStatus.INVALID_SORT_TYPE);
        }
        VacancyDoc vacancyDoc = validateMatchingRequest(userProfileId, vacancyId, size);

        MatchingCandidateConfig config = initMatchingCandidateConfig();

        CandidatesResp response = new CandidatesResp();
        if (sortType == 1) {
            Page<UserCvDoc> result = userCvDocService.findMatchingCvForVacancySortBySalary(vacancyDoc, offset, size, rejectedLimitTime, config);
            List<UserCurriculumVitaeDTO> userCvs = getUserCvDTO(result, getLocale(authentication));

            response.setUserCurriculumVitaes(userCvs);
        } else {
            List<UserCvDocAggregation> aggregations = userCvDocService.findMatchingCvForVacancySortByExperience(vacancyDoc, offset, size, rejectedLimitTime, config);

            List<UserCurriculumVitaeDTO> userCvs = getUserCvDTO(aggregations, getLocale(authentication));
            response.setUserCurriculumVitaes(userCvs);
        }

        List<UserCvDocAggregation> maxExpCv = userCvDocService.findMatchingCvForVacancySortByExperience(vacancyDoc, 0, 1, rejectedLimitTime, config);
        boostActorManager.updateMessageCenterDocInMongoActor(vacancyDoc);
        return getUserCVsResponse(size, offset, vacancyDoc, config, response, maxExpCv);
    }

    @Override
    public BaseResp findMatchingCvForVacancyWithAssessmentSort(long userProfileId,
                                                               long vacancyId,
                                                               int size,
                                                               int offset,
                                                               long assessmentId,
                                                               Authentication authentication) {
        VacancyDoc vacancyDoc = validateMatchingRequest(userProfileId, vacancyId, size);
        MatchingCandidateConfig config = initMatchingCandidateConfig();
        boolean isNoneAssessment = vacancyDoc.getQualifications().stream()
                .filter(Objects::nonNull)
                .noneMatch(it -> it.getAssessment().getId().equals(assessmentId));
        if (isNoneAssessment) {
            throw new InvalidParamException(ResponseStatus.ASSESSMENT_NOT_BELONG_TO_VACANCY);
        }
        CandidatesResp response = new CandidatesResp();
        List<UserCvDocAggregation> userCvDocs = userCvDocService.findMatchingCvForVacancySortByAssessment(
                vacancyDoc, assessmentId, offset, size, rejectedLimitTime, config);
        List<UserCurriculumVitaeDTO> userCvs = getUserCvDTO(userCvDocs, getLocale(authentication));
        response.setUserCurriculumVitaes(userCvs);

        List<UserCvDocAggregation> maxExpCv = userCvDocService.findMatchingCvForVacancySortByExperience(
                vacancyDoc, 0, 1, rejectedLimitTime, config);

        boostActorManager.updateMessageCenterDocInMongoActor(vacancyDoc);
        return getUserCVsResponse(size, offset, vacancyDoc, config, response, maxExpCv);
    }

    private BaseResp getUserCVsResponse(int size, int offset, VacancyDoc vacancyDoc, MatchingCandidateConfig config, CandidatesResp response, List<UserCvDocAggregation> maxExpCv) {
        if (CollectionUtils.isNotEmpty(maxExpCv)) {
            Date minDate = Optional.ofNullable(maxExpCv.get(0).getMinStartDate()).orElseGet(Date::new);
            Date maxDate = Optional.ofNullable(maxExpCv.get(0).getMaxEndDate()).orElseGet(Date::new);
            Period diff = Period.between(LocalDate.ofEpochDay(getEpochDayOfDate(minDate.getTime())),
                    LocalDate.ofEpochDay(getEpochDayOfDate(maxDate.getTime())));
            response.setMaxExperience(Math.toIntExact(diff.toTotalMonths()));
        }

        UserCvDoc userCvDoc = userCvDocService.findMaxSalaryMatchingCvForVacancy(vacancyDoc, rejectedLimitTime, config);
        Optional.ofNullable(userCvDoc).ifPresent(u -> response.setMaxSalary(u.getMaxSalary()));

        if (response.getUserCurriculumVitaes().size() < size) {
            response.setHasMore(false);
        } else {
            long countCandidate = userCvDocService.countMatchingCvForVacancy(vacancyDoc, rejectedLimitTime, config);
            long realItem = offset + response.getUserCurriculumVitaes().size();
            response.setHasMore(realItem < countCandidate);
        }

        return new BaseResp<>(response);
    }

    private long getEpochDayOfDate(long timestamp) {
        return (timestamp - (new Date(0).getTime()))/ DateUtils.ONE_DAY_IN_DAY_UNIT;
    }

    private VacancyDoc validateMatchingRequest(long userProfileId, long vacancyId, int size) {
        if (size < 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_PAGINATION);
        }
        VacancyDoc vacancyDoc = vacancyDocService.findById(vacancyId);
        if (Objects.isNull(vacancyDoc)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_VACANCY);
        } else if (Const.Vacancy.Status.INACTIVE == vacancyDoc.getVacancyStatus()) {
            throw new NoPermissionException(ResponseStatus.VACANCY_IS_INACTIVE);
        } else if (Const.Vacancy.Status.SUSPEND == vacancyDoc.getVacancyStatus()) {
            throw new NoPermissionException(ResponseStatus.VACANCY_IS_SUSPENDED);
        }
        businessValidatorService.checkPermissionOnVacancy(vacancyDoc, userProfileId);
        return vacancyDoc;
    }

    private MatchingCandidateConfig initMatchingCandidateConfig() {
        MatchingCandidateConfig config = new MatchingCandidateConfig();
        config.setBoostScoreEnabled(boostScoreEnabled);
        config.setVacancyJobAlphaTest(vacancyJobAlphaTest);
        config.setExpiredDays(expiredDays);
        return config;
    }

    private List<UserCurriculumVitaeDTO> getUserCvDTO(Page<UserCvDoc> result, String locale) {
        Date now = new Date();
        List<UserCurriculumVitaeDTO> userCvs = Optional.ofNullable(result).map(Page::getContent).filter(CollectionUtils::isNotEmpty)
                .map(it -> it.stream()
                        .map(userCvDoc -> new UserCurriculumVitaeDTO(userCvDoc, expiredDays, locale))
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
        userCvs.stream()
                .map(UserCurriculumVitaeDTO::getQualifications)
                .filter(CollectionUtils::isNotEmpty)
                .forEach(it -> it.removeIf(qualification -> qualification.getExpiredTime().before(now)));
        return userCvs;
    }

    private List<UserCurriculumVitaeDTO> getUserCvDTO(List<UserCvDocAggregation> result, String locale) {
        Date now = new Date();
        List<UserCurriculumVitaeDTO> userCvs = Optional.ofNullable(result).filter(CollectionUtils::isNotEmpty)
                .map(it -> it.stream()
                .map(userCvDoc -> new UserCurriculumVitaeDTO(userCvDoc, expiredDays, locale))
                .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
        userCvs.stream()
                .map(UserCurriculumVitaeDTO::getQualifications)
                .filter(CollectionUtils::isNotEmpty)
                .forEach(it -> it.removeIf(qualification -> qualification.getExpiredTime().before(now)));
        return userCvs;
    }
}
