package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.PersonalAssessmentQuestion;

import java.util.List;

public interface PersonalAssessmentQuestionService {
    List<PersonalAssessmentQuestion> getByPersonalAssessmentId(long personalAssessmentId);
}
