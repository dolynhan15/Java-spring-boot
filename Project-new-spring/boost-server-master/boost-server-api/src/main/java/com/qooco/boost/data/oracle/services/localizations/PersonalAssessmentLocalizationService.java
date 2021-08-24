package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.PersonalAssessmentLocalization;

import java.util.List;

public interface PersonalAssessmentLocalizationService {
    Iterable<PersonalAssessmentLocalization> saveAll(Iterable<PersonalAssessmentLocalization> personalAssessmentLocalizations);

    List<PersonalAssessmentLocalization> findAll();
}
