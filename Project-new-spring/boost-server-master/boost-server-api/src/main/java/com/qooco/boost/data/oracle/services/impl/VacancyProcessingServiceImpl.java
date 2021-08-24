package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.model.count.CountCandidateProcessing;
import com.qooco.boost.data.oracle.entities.VacancyProcessing;
import com.qooco.boost.data.oracle.reposistories.views.VacancyProcessingRepository;
import com.qooco.boost.data.oracle.services.VacancyProcessingService;
import com.qooco.boost.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VacancyProcessingServiceImpl implements VacancyProcessingService {
    @Autowired
    private VacancyProcessingRepository repository;
    @Override
    public List<VacancyProcessing> save(Iterable<VacancyProcessing> vacancyProcessings) {
        return repository.saveAll(vacancyProcessings);
    }

    @Override
    public int countByCompanyInDuration(long companyId, long startDate, long endDate) {
        return repository.countByCompanyInDuration(companyId, DateUtils.toUtcForOracle(new Date(startDate)), DateUtils.toUtcForOracle(new Date(endDate)));
    }

    @Override
    public List<CountCandidateProcessing> countByStaffInDurationInEachDay(long staffId, long startDate, long endDate) {
        int days = DateUtils.countDays(startDate, endDate);
        List<Object[]> result = repository.countByStaffInDurationInEachDay(
                staffId, DateUtils.toUtcForOracle(new Date(startDate)), DateUtils.toUtcForOracle(new Date(endDate)), days);
        return result.stream().map(
                item -> new CountCandidateProcessing((Date) item[0], ((BigDecimal) item[1]).intValue(), ((BigDecimal) item[2]).intValue())).collect(Collectors.toList());
    }

    @Override
    public void deleteAllByVacancyId(List<Long> vacancyIds) {
        repository.deleteAllByVacancyId(vacancyIds);
    }
}
