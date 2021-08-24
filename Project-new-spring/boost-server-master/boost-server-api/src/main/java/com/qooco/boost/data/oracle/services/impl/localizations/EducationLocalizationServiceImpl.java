package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.EducationLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.EducationLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.EducationLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationLocalizationServiceImpl implements EducationLocalizationService {

    @Autowired
    private EducationLocalizationRepository repository;
    @Override
    public Iterable<EducationLocalization> saveAll(Iterable<EducationLocalization> educationLocalizations) {
        return repository.saveAll(educationLocalizations);
    }

    @Override
    public List<EducationLocalization> findAll() {
        return repository.findAll();
    }
}
