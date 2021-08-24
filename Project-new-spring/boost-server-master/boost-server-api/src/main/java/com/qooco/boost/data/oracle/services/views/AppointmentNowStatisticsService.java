package com.qooco.boost.data.oracle.services.views;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/3/2018 - 1:27 PM
 */

import com.qooco.boost.data.oracle.entities.views.AppointmentNowStatistics;

import java.util.List;

public interface AppointmentNowStatisticsService {

    List<AppointmentNowStatistics> findAll();
}
