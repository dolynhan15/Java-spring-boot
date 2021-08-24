package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.CurrencyLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.CurrencyLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.CurrencyLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyLocalizationServiceImpl implements CurrencyLocalizationService {

    @Autowired
    private CurrencyLocalizationRepository repository;
    @Override
    public Iterable<CurrencyLocalization> saveAll(Iterable<CurrencyLocalization> currencyLocalizations) {
        return repository.saveAll(currencyLocalizations);
    }

    @Override
    public List<CurrencyLocalization> findAll() {
        return repository.findAll();
    }
}
