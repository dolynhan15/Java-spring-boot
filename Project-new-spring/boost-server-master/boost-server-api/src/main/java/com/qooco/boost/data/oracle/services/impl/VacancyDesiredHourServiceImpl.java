package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.VacancyDesiredHour;
import com.qooco.boost.data.oracle.reposistories.VacancyDesiredHourRepository;
import com.qooco.boost.data.oracle.services.VacancyDesiredHourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 4:49 PM
 */
@Service
public class VacancyDesiredHourServiceImpl implements VacancyDesiredHourService {
    @Autowired
    private VacancyDesiredHourRepository vacancyDesiredHourRepository;

    @Override
    public List<VacancyDesiredHour> findByVacancyId(Long vacancyId) {
        return vacancyDesiredHourRepository.findByVacancyId(vacancyId);
    }

    @Override
    public VacancyDesiredHour save(VacancyDesiredHour vacancyDesiredHour) {
        return vacancyDesiredHourRepository.save(vacancyDesiredHour);
    }

    @Override
    public List<VacancyDesiredHour> save(List<VacancyDesiredHour> vacancyDesiredHours) {
        return Lists.newArrayList(vacancyDesiredHourRepository.saveAll(vacancyDesiredHours));
    }

    @Override
    public void deleteByVacancyId(Long vacancyId) {
        vacancyDesiredHourRepository.deleteByVacancyId(vacancyId);
    }
}
