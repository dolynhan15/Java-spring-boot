package com.qooco.boost.data.oracle.reposistories.views;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/3/2018 - 1:27 PM
 */

import com.qooco.boost.data.oracle.entities.views.AppointmentNowStatistics;
import com.qooco.boost.data.oracle.reposistories.Boot2JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentNowStatisticsRepository extends Boot2JpaRepository<AppointmentNowStatistics, Long> {

}
