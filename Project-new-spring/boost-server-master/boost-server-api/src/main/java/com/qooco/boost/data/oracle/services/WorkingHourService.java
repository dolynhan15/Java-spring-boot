package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.WorkingHour;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 10:32 AM
*/
public interface WorkingHourService {
    WorkingHour findById(long workingHourId);

    List<WorkingHour> findByIds(long[] lstWorkingHourId);

    List<WorkingHour> findByWorkingType(boolean workingType);

    List<WorkingHour> getAll();
    Boolean exist(Long[] ids);
}
