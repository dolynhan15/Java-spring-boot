package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.model.count.CountCandidateProcessing;
import com.qooco.boost.data.oracle.entities.VacancyProcessing;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 10:32 AM
*/
public interface VacancyProcessingService {
    List<VacancyProcessing> save(Iterable<VacancyProcessing> vacancyProcessings);

    int countByCompanyInDuration(long companyId, long startDate, long endDate);

    List<CountCandidateProcessing> countByStaffInDurationInEachDay(long staffId, long startDate, long endDate);

    void deleteAllByVacancyId(List<Long> vacancyIds);
}
