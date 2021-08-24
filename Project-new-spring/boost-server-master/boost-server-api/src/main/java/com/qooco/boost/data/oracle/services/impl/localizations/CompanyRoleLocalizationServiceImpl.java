package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.CompanyRoleLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.CompanyRoleLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.CompanyRoleLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyRoleLocalizationServiceImpl implements CompanyRoleLocalizationService {

    @Autowired
    private CompanyRoleLocalizationRepository repository;

    @Override
    public Iterable<CompanyRoleLocalization> saveAll(Iterable<CompanyRoleLocalization> companyRoleLocalizations) {
        return repository.saveAll(companyRoleLocalizations);
    }

    @Override
    public List<CompanyRoleLocalization> findAll() {
        return repository.findAll();
    }
}
