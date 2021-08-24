package com.qooco.boost.threads.services;

import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.utils.MatchingCandidateConfig;

public interface FeedbackDataSelectUserService {

    void dataFeedbackSearchRangeTooSmall(VacancyDoc vacancyDoc, MatchingCandidateConfig candidateConfig);

    void dataFeedbackQualificationTooHigh(VacancyDoc vacancyDoc, MatchingCandidateConfig candidateConfig);

    void dataFeedbackQualificationTooLow(VacancyDoc vacancyDoc, MatchingCandidateConfig candidateConfig);

    void dataFeedbackSalaryTooHigh(VacancyDoc vacancyDoc, MatchingCandidateConfig candidateConfig);

    void dataFeedbackSalaryTooLow(VacancyDoc vacancyDoc, MatchingCandidateConfig candidateConfig);
}
