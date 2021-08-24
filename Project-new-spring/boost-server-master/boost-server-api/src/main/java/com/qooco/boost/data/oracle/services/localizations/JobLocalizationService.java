package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.JobLocalization;

import java.util.List;

public interface JobLocalizationService {
    Iterable<JobLocalization> saveAll(Iterable<JobLocalization> jobLocalizations);

    List<JobLocalization> findAll();
}
