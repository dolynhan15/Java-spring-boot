package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.model.count.CountVacancySeat;
import com.qooco.boost.data.oracle.entities.VacancySeat;

import java.util.List;

public interface VacancySeatService {
    List<CountVacancySeat> countVacancySeatInRangeByCompany(long companyId, long startDate, long endDate);

    List<VacancySeat> save(List<VacancySeat> vacancySeats);

    List<CountVacancySeat> countVacancySeatInRangeByStaff(long staffId, long startDate, long endDate);

    void deleteAllByVacancyId(List<Long> vacancyIds);
}
