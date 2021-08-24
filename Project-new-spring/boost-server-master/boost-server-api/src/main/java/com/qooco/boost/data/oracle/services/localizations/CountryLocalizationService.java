package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.CountryLocalization;

import java.util.List;

public interface CountryLocalizationService {
    Iterable<CountryLocalization> saveAll(Iterable<CountryLocalization> countryLocalizations);

    List<CountryLocalization> findAll();
}
