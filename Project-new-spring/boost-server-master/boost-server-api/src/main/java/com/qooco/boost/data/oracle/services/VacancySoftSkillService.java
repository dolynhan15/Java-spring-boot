package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.VacancySoftSkill;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 3:32 PM
 */
public interface VacancySoftSkillService {
    List<VacancySoftSkill> findByVacancyId(Long vacancyId);

    List<VacancySoftSkill> save(List<VacancySoftSkill> vacancySoftSkills);

    VacancySoftSkill save(VacancySoftSkill vacancySoftSkill);

    void deleteByVacancyId(Long vacancyId);
}
