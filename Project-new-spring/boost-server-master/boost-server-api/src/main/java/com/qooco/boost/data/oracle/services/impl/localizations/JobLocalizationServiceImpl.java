package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.JobLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.JobLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.JobLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobLocalizationServiceImpl implements JobLocalizationService {

    @Autowired
    private JobLocalizationRepository repository;
    @Override
    public Iterable<JobLocalization> saveAll(Iterable<JobLocalization> jobLocalizations) {
        return repository.saveAll(jobLocalizations);
    }

    @Override
    public List<JobLocalization> findAll() {
        return repository.findAll();
    }
}
