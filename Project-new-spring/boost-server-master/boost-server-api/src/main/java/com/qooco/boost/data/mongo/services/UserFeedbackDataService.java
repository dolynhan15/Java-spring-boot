package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.model.QualificationAverage;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.utils.MatchingCandidateConfig;

import java.util.List;

public interface UserFeedbackDataService {

    List<UserCvDoc> findMatchingCvForVacancyNotEnoughExperience(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config, List<Long> excludeUserCvIds);

    List<UserCvDoc> findMatchingCvForVacancySalaryTooHigh(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config);

    List<UserCvDoc> findMatchingCvForVacancyNoQualification(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config);

    List<QualificationAverage> getAverageLevelByVacancy(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config);

    long countMatchingCvForVacancyIgnoreQualification(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config);
}
