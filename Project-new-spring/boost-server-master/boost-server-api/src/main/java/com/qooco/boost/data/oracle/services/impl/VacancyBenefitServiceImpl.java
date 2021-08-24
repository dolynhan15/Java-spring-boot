package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.VacancyBenefit;
import com.qooco.boost.data.oracle.reposistories.VacancyBenefitRepository;
import com.qooco.boost.data.oracle.services.VacancyBenefitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 4:53 PM
 */

@Service
public class VacancyBenefitServiceImpl implements VacancyBenefitService {
    @Autowired
    private VacancyBenefitRepository vacancyBenefitRepository;

    @Override
    public List<VacancyBenefit> findByVacancyId(Long vacancyId) {
        return vacancyBenefitRepository.findByVacancyId(vacancyId);
    }

    @Override
    public VacancyBenefit save(VacancyBenefit vacancyBenefit) {
        return vacancyBenefitRepository.save(vacancyBenefit);
    }

    @Override
    public List<VacancyBenefit> save(List<VacancyBenefit> vacancyBenefits) {
        return Lists.newArrayList(vacancyBenefitRepository.saveAll(vacancyBenefits));
    }

    @Override
    public void deleteByVacancyId(Long vacancyId) {
        vacancyBenefitRepository.deleteByVacancyId(vacancyId);
    }
}
