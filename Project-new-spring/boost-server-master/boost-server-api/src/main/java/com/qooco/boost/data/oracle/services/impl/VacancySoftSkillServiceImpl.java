package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.VacancySoftSkill;
import com.qooco.boost.data.oracle.reposistories.VacancySoftSkillRepository;
import com.qooco.boost.data.oracle.services.VacancySoftSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 4:27 PM
 */
@Service
public class VacancySoftSkillServiceImpl implements VacancySoftSkillService {
    @Autowired
    private VacancySoftSkillRepository vacancySoftSkillRepository;

    @Override
    public List<VacancySoftSkill> findByVacancyId(Long vacancyId) {
        return vacancySoftSkillRepository.findByVacancyId(vacancyId);
    }

    @Override
    public List<VacancySoftSkill> save(List<VacancySoftSkill> vacancySoftSkills) {
        return Lists.newArrayList(vacancySoftSkillRepository.saveAll(vacancySoftSkills));
    }

    @Override
    public VacancySoftSkill save(VacancySoftSkill vacancySoftSkill) {
        return vacancySoftSkillRepository.save(vacancySoftSkill);
    }

    @Override
    public void deleteByVacancyId(Long vacancyId) {
        vacancySoftSkillRepository.deleteByVacancyId(vacancyId);
    }
}
