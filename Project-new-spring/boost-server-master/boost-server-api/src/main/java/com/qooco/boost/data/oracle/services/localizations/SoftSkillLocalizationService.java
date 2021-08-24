package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.SoftSkillLocalization;

import java.util.List;

public interface SoftSkillLocalizationService {
    Iterable<SoftSkillLocalization> saveAll(Iterable<SoftSkillLocalization> softSkillLocalizations);

    List<SoftSkillLocalization> findAll();
}
