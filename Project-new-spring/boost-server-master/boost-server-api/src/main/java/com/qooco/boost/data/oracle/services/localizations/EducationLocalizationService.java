package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.EducationLocalization;

import java.util.List;

public interface EducationLocalizationService {
    Iterable<EducationLocalization> saveAll(Iterable<EducationLocalization> educationLocalizations);

    List<EducationLocalization> findAll();
}
