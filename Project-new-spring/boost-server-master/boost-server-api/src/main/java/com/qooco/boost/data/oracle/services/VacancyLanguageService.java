package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.VacancyLanguage;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 3:30 PM
 */
public interface VacancyLanguageService {
    List<VacancyLanguage> findByVacancyId(Long vacancyId);

    List<VacancyLanguage> save(List<VacancyLanguage> vacancyLanguages);

    void deleteByVacancyId(Long vacancyId);


}
