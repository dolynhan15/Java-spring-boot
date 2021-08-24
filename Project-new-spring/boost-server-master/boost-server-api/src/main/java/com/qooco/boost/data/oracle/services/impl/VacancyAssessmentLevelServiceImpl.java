package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.VacancyAssessmentLevel;
import com.qooco.boost.data.oracle.reposistories.VacancyAssessmentLevelRepository;
import com.qooco.boost.data.oracle.services.VacancyAssessmentLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/10/2018 - 11:54 AM
 */
@Service
public class VacancyAssessmentLevelServiceImpl implements VacancyAssessmentLevelService {
    @Autowired
    private VacancyAssessmentLevelRepository repository;

    @Override
    public List<VacancyAssessmentLevel> findByVacancyId(Long vacancyId) {
        return repository.findByVacancyId(vacancyId);
    }

    @Override
    public VacancyAssessmentLevel save(VacancyAssessmentLevel vacancyAssessmentLevel) {
        return repository.save(vacancyAssessmentLevel);
    }

    @Override
    public List<VacancyAssessmentLevel> save(List<VacancyAssessmentLevel> vacancyAssessmentLevels) {
        return Lists.newArrayList(repository.saveAll(vacancyAssessmentLevels));
    }

    @Override
    public void deleteByVacancyId(Long vacancyId) {
        repository.deleteByVacancyId(vacancyId);
    }
}
