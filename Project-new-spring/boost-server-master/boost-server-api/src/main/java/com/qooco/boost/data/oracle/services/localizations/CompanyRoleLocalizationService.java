package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.CompanyRoleLocalization;

import java.util.List;

public interface CompanyRoleLocalizationService {
    Iterable<CompanyRoleLocalization> saveAll(Iterable<CompanyRoleLocalization> companyRoleLocalizations);

    List<CompanyRoleLocalization> findAll();
}
