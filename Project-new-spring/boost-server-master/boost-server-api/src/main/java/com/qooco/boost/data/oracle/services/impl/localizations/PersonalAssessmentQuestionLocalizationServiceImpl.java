package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.PersonalAssessmentQuestionLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.PersonalAssessmentQuestionLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.PersonalAssessmentQuestionLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonalAssessmentQuestionLocalizationServiceImpl implements PersonalAssessmentQuestionLocalizationService {

    @Autowired
    private PersonalAssessmentQuestionLocalizationRepository repository;
    @Override
    public Iterable<PersonalAssessmentQuestionLocalization> saveAll(Iterable<PersonalAssessmentQuestionLocalization> personalAssessmentQuestionLocalizations) {
        return repository.saveAll(personalAssessmentQuestionLocalizations);
    }

    @Override
    public List<PersonalAssessmentQuestionLocalization> findAll() {
        return repository.findAll();
    }
}
