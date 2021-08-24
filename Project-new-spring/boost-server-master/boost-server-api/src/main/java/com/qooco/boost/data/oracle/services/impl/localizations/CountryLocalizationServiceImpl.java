package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.CountryLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.CountryLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.CountryLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryLocalizationServiceImpl implements CountryLocalizationService {

    @Autowired
    private CountryLocalizationRepository repository;
    @Override
    public Iterable<CountryLocalization> saveAll(Iterable<CountryLocalization> countryLocalizations) {
        return repository.saveAll(countryLocalizations);
    }

    @Override
    public List<CountryLocalization> findAll() {
        return repository.findAll();
    }
}
