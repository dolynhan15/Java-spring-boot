package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.LanguageLocalization;

import java.util.List;

public interface LanguageLocalizationService {
    Iterable<LanguageLocalization> saveAll(Iterable<LanguageLocalization> languageLocalizations);

    List<LanguageLocalization> findAll();
}
