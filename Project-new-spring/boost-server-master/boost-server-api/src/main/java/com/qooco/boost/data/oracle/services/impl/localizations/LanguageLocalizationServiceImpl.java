package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.LanguageLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.LanguageLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.LanguageLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageLocalizationServiceImpl implements LanguageLocalizationService {

    @Autowired
    private LanguageLocalizationRepository repository;
    @Override
    public Iterable<LanguageLocalization> saveAll(Iterable<LanguageLocalization> LanguageLocalizations) {
        return repository.saveAll(LanguageLocalizations);
    }

    @Override
    public List<LanguageLocalization> findAll() {
        return repository.findAll();
    }
}
