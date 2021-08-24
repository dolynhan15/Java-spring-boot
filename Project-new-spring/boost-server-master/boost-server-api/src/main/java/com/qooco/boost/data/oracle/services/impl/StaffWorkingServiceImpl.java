package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.model.count.CountByDate;
import com.qooco.boost.data.oracle.entities.StaffWorking;
import com.qooco.boost.data.oracle.reposistories.StaffWorkingRepository;
import com.qooco.boost.data.oracle.services.StaffWorkingService;
import com.qooco.boost.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StaffWorkingServiceImpl implements StaffWorkingService {

    @Autowired
    private StaffWorkingRepository repository;

    @Override
    public List<StaffWorking> save(Iterable<StaffWorking> staffWorkings) {
        return repository.saveAll(staffWorkings);
    }

    @Override
    public int sumByCompanyInDuration(long companyId, long startDate, long endDate) {
        Integer sum = repository.sumByCompanyInDuration(companyId, DateUtils.toUtcForOracle(new Date(startDate)), DateUtils.toUtcForOracle(new Date(endDate)));
        return Optional.ofNullable(sum).orElse(0);
    }

    @Override
    public List<CountByDate> sumByStaffInDurationInEachDay(long staffId, long startDate, long endDate) {
        int days = DateUtils.countDays(startDate, endDate);
        List<Object[]> result = repository.sumByStaffInDurationInEachDay(
                staffId, DateUtils.toUtcForOracle(new Date(startDate)), DateUtils.toUtcForOracle(new Date(endDate)), days);
        return result.stream().map(
                item -> new CountByDate((Date) item[0], ((BigDecimal) item[1]).intValue())).collect(Collectors.toList());
    }

    @Override
    public void deleteAllByStaffId(List<Long> staffIds) {
        repository.deleteAllByStaffId(staffIds);
    }
}
