package com.qooco.boost.threads.services;

import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.utils.MatchingCandidateConfig;

public interface FeedbackDataProfileUserService {
    void dataFeedbackChangeLocateSuggestion(UserCvDoc userCvDoc, MatchingCandidateConfig candidateConfig);

    void dataFeedbackNoQualification(MatchingCandidateConfig candidateConfig);

    void dataFeedbackLowQualifications(UserCvDoc userCvDoc, MatchingCandidateConfig candidateConfig);

    void dataFeedbackSeatsClosed(UserCvDoc userCvDoc);

    void dataFeedbackCodesShared();

    void dataFeedbackNotEnoughExperience(MatchingCandidateConfig candidateConfig);

    void dataFeedbackSalaryTooHigh(MatchingCandidateConfig candidateConfig);
}
