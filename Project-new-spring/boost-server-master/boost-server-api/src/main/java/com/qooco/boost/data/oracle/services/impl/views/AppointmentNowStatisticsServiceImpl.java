package com.qooco.boost.data.oracle.services.impl.views;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.views.AppointmentNowStatistics;
import com.qooco.boost.data.oracle.reposistories.views.AppointmentNowStatisticsRepository;
import com.qooco.boost.data.oracle.services.views.AppointmentNowStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 4/10/2019 - 2:31 PM
*/
@Service
public class AppointmentNowStatisticsServiceImpl implements AppointmentNowStatisticsService {

    @Autowired
    private AppointmentNowStatisticsRepository repository;
    @Override
    public List<AppointmentNowStatistics> findAll() {
        return Lists.newArrayList( repository.findAll().iterator());
    }
}
