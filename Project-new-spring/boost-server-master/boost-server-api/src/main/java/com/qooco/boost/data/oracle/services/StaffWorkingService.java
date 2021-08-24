package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.model.count.CountByDate;
import com.qooco.boost.data.oracle.entities.StaffWorking;

import java.util.List;

public interface StaffWorkingService {
    List<StaffWorking> save(Iterable<StaffWorking> staffWorkings);

    int sumByCompanyInDuration(long companyId, long startDate, long endDate);

    List<CountByDate> sumByStaffInDurationInEachDay(long staffId, long startDate, long endDate);

    void deleteAllByStaffId(List<Long> staffIds);
}
