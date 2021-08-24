package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.VacancyBenefit;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 4:21 PM
 */
public interface VacancyBenefitService {
    List<VacancyBenefit> findByVacancyId(Long vacancyId);

    VacancyBenefit save(VacancyBenefit vacancyBenefit);

    List<VacancyBenefit> save(List<VacancyBenefit> vacancyBenefits);

    void deleteByVacancyId(Long vacancyId);
}
