package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.VacancyDesiredHour;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 3:38 PM
 */
public interface VacancyDesiredHourService {
    List<VacancyDesiredHour> findByVacancyId(Long vacancyId);

    VacancyDesiredHour save(VacancyDesiredHour vacancyDesiredHour);

    List<VacancyDesiredHour> save(List<VacancyDesiredHour> vacancyDesiredHours);

    void deleteByVacancyId(Long vacancyId);
}
