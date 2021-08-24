package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.PersonalAssessmentQuestionLocalization;

import java.util.List;

public interface PersonalAssessmentQuestionLocalizationService {
    Iterable<PersonalAssessmentQuestionLocalization> saveAll(Iterable<PersonalAssessmentQuestionLocalization> personalAssessmentQuestionLocalizations);

    List<PersonalAssessmentQuestionLocalization> findAll();
}
