package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.SoftSkillLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.SoftSkillLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.SoftSkillLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SoftSkillLocalizationServiceImpl implements SoftSkillLocalizationService {

    @Autowired
    private SoftSkillLocalizationRepository repository;
    @Override
    public Iterable<SoftSkillLocalization> saveAll(Iterable<SoftSkillLocalization> softSkillLocalizations) {
        return repository.saveAll(softSkillLocalizations);
    }

    @Override
    public List<SoftSkillLocalization> findAll() {
        return repository.findAll();
    }
}
