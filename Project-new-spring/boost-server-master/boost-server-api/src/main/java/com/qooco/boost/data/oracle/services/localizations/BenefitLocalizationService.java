package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.BenefitLocalization;

import java.util.List;

public interface BenefitLocalizationService {
    Iterable<BenefitLocalization> saveAll(Iterable<BenefitLocalization> benefitLocalizations);

    List<BenefitLocalization> findAll();
}
