package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.VacancyLanguage;
import com.qooco.boost.data.oracle.reposistories.VacancyLanguageRepository;
import com.qooco.boost.data.oracle.services.VacancyLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 4:39 PM
 */
@Service
public class VacancyLanguageServiceImpl implements VacancyLanguageService {
    @Autowired
    private VacancyLanguageRepository vacancyLanguageRepository;

    @Override
    public List<VacancyLanguage> findByVacancyId(Long vacancyId) {
        return vacancyLanguageRepository.findByVacancyId(vacancyId);
    }
    
    @Override
    public List<VacancyLanguage> save(List<VacancyLanguage> vacancyLanguages) {
        return Lists.newArrayList(vacancyLanguageRepository.saveAll(vacancyLanguages));
    }

    @Override
    public void deleteByVacancyId(Long vacancyId) {
        vacancyLanguageRepository.deleteByVacancyId(vacancyId);
    }


}
