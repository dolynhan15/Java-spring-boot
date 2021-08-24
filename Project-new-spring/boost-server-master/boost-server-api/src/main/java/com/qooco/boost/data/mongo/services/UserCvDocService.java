package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.model.UserCvDocAggregation;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.utils.MatchingCandidateConfig;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserCvDocService extends DocService<UserCvDoc, Long> {

    UserCvDoc findByUserProfileId(long userProfileId);

    List<UserCvDoc> findByProvince(Long provinceId);

    List<UserCvDoc> findByProvinceAndProfileStrength(Long provinceId, int profileStrength);

    List<UserCvDoc> findByUserProfileId(List<Long> userProfileIds);

    List<UserCvDoc> getUserCvNotSyncSuccessful();

    List<UserCvDoc> getUserCvNotSyncWorkingDateExperience(int limit, List<Long> exceptedUserIds);

    Page<UserCvDoc> findMatchingCvForVacancy(VacancyDoc vacancyDoc, int page, int size, int limitTime, MatchingCandidateConfig config);

    List<UserCvDocAggregation> findMatchingCvForVacancyWithPreferredHotelSorting(VacancyDoc vacancyDoc, int page, int size, int limitTime, MatchingCandidateConfig config);

    List<UserCvDocAggregation> findMatchingCvForVacancySortByExperience(VacancyDoc vacancyDoc, int offset, int size, int limitTime, MatchingCandidateConfig config);

    List<UserCvDocAggregation> findMatchingCvForVacancySortByAssessment(VacancyDoc vacancyDoc, long assessmentId, int offset, int size, int limitTime, MatchingCandidateConfig config);

    Page<UserCvDoc> findMatchingCvForVacancySortBySalary(VacancyDoc vacancyDoc, int offset, int size, int limitTime, MatchingCandidateConfig config);

    UserCvDoc findMaxSalaryMatchingCvForVacancy(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config);

    long countMatchingCvForVacancy(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config);

    long countMatchingCvForVacancyIgnoreSalary(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config);

    List<UserCvDoc> getByIdGreaterThan(Long prevId, int limit);

    List<UserCvDoc> findOpenVacancyByCurrencyIdGreaterThan(long currencyId, Long prevId, int limit);

    List<Long> findIdByIds(List<Long> Ids);

    void addDefaultValueForHasPersonality();

    void updateHasPersonality(long id, boolean hasPersonality);


    boolean isSalaryTooLow(VacancyDoc vacancyDoc, int limitTime,
                           MatchingCandidateConfig config);

    boolean isSalaryTooHigh(VacancyDoc vacancyDoc, int limitTime,
                            MatchingCandidateConfig config);

    void updateCurrencyAndSalaryInUsd(Currency currency, List<UserCvDoc> userCvDocs);

    void addDefaultArrayForPreferredHotels();
}
