package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.CurrencyLocalization;

import java.util.List;

public interface CurrencyLocalizationService {
    Iterable<CurrencyLocalization> saveAll(Iterable<CurrencyLocalization> currencyLocalizations);

    List<CurrencyLocalization> findAll();
}
