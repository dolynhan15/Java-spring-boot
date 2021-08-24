package com.qooco.boost.data.oracle.services.views;

import java.util.List;

public interface ViewStatisticVacancySeatService {
    List<Long[]> findTopEmployeeByCompany(Long company, Long startDate, Long endDate, int limit);

    int countMaxClosedSeatsInCompany(Long company, Long startDate, Long endDate);
}
