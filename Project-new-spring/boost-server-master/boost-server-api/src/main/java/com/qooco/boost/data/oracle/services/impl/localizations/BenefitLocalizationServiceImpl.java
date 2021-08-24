package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.BenefitLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.BenefitLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.BenefitLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BenefitLocalizationServiceImpl implements BenefitLocalizationService {

    @Autowired
    private BenefitLocalizationRepository repository;
    @Override
    public Iterable<BenefitLocalization> saveAll(Iterable<BenefitLocalization> jobLocalizations) {
        return repository.saveAll(jobLocalizations);
    }

    @Override
    public List<BenefitLocalization> findAll() {
        return repository.findAll();
    }
}
