package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.ProvinceLocalization;

import java.util.List;

public interface ProvinceLocalizationService {
    Iterable<ProvinceLocalization> saveAll(Iterable<ProvinceLocalization> provinceLocalizations);

    List<ProvinceLocalization> findAll();
}
