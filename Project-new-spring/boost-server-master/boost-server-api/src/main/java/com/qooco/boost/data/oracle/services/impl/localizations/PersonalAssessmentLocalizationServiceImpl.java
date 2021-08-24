package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.PersonalAssessmentLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.PersonalAssessmentLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.PersonalAssessmentLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonalAssessmentLocalizationServiceImpl implements PersonalAssessmentLocalizationService {

    @Autowired
    private PersonalAssessmentLocalizationRepository repository;
    @Override
    public Iterable<PersonalAssessmentLocalization> saveAll(Iterable<PersonalAssessmentLocalization> personalAssessmentLocalizations) {
        return repository.saveAll(personalAssessmentLocalizations);
    }

    @Override
    public List<PersonalAssessmentLocalization> findAll() {
        return repository.findAll();
    }
}
