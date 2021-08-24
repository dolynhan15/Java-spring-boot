package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.VacancyAssessmentLevel;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/10/2018 - 11:52 AM
 */
public interface VacancyAssessmentLevelService {
    List<VacancyAssessmentLevel> findByVacancyId(Long vacancyId);

    VacancyAssessmentLevel save(VacancyAssessmentLevel vacancyAssessmentLevel);

    List<VacancyAssessmentLevel> save(List<VacancyAssessmentLevel> vacancyAssessmentLevels);

    void deleteByVacancyId(Long vacancyId);
}
