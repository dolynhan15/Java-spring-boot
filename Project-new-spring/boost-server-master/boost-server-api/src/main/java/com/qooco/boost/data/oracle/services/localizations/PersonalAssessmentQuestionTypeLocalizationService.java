package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.PersonalAssessmentQuestionTypeLocalization;

import java.util.List;

public interface PersonalAssessmentQuestionTypeLocalizationService {
    Iterable<PersonalAssessmentQuestionTypeLocalization> saveAll(Iterable<PersonalAssessmentQuestionTypeLocalization> personalAssessmentQuestionTypeLocalizations);

    List<PersonalAssessmentQuestionTypeLocalization> findAll();
}
