package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.PersonalAssessmentQuestionTypeLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.PersonalAssessmentQuestionTypeLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.PersonalAssessmentQuestionTypeLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonalAssessmentQuestionTypeLocalizationServiceImpl implements PersonalAssessmentQuestionTypeLocalizationService {

    @Autowired
    private PersonalAssessmentQuestionTypeLocalizationRepository repository;
    @Override
    public Iterable<PersonalAssessmentQuestionTypeLocalization> saveAll(Iterable<PersonalAssessmentQuestionTypeLocalization> assessmentQuestionTypeLocalizations) {
        return repository.saveAll(assessmentQuestionTypeLocalizations);
    }

    @Override
    public List<PersonalAssessmentQuestionTypeLocalization> findAll() {
        return repository.findAll();
    }
}
