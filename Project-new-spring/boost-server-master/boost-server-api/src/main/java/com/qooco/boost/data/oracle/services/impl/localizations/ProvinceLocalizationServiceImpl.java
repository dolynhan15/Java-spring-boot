package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.ProvinceLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.ProvinceLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.ProvinceLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinceLocalizationServiceImpl implements ProvinceLocalizationService {

    @Autowired
    private ProvinceLocalizationRepository repository;
    @Override
    public Iterable<ProvinceLocalization> saveAll(Iterable<ProvinceLocalization> provinceLocalizations) {
        return repository.saveAll(provinceLocalizations);
    }

    @Override
    public List<ProvinceLocalization> findAll() {
        return repository.findAll();
    }
}
