package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.model.count.CountVacancySeat;
import com.qooco.boost.data.oracle.entities.VacancySeat;
import com.qooco.boost.data.oracle.reposistories.VacancySeatRepository;
import com.qooco.boost.data.oracle.services.VacancySeatService;
import com.qooco.boost.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VacancySeatServiceImpl implements VacancySeatService {
    @Autowired
    private VacancySeatRepository repository;

    @Override
    public List<CountVacancySeat> countVacancySeatInRangeByCompany(long companyId, long startDate, long endDate) {
        int days = DateUtils.countDays(startDate, endDate);
        List<Object[]> result = repository.countVacancySeatInRangeByCompany(
                companyId, DateUtils.toUtcForOracle(new Date(startDate)), DateUtils.toUtcForOracle(new Date(endDate)), days);
        return result.stream().map(
                item -> new CountVacancySeat((Date) item[0], ((BigDecimal) item[1]).intValue(), (((BigDecimal) item[2]).intValue()))).collect(Collectors.toList());
    }

    @Override
    public List<VacancySeat> save(List<VacancySeat> vacancySeats) {
        return repository.saveAll(vacancySeats);
    }

    @Override
    public List<CountVacancySeat> countVacancySeatInRangeByStaff(long staffId, long startDate, long endDate) {
        int days = DateUtils.countDays(startDate, endDate);
        List<Object[]> resultClosedSeat = repository.countClosedSeatInRangeByStaff(
                staffId, DateUtils.toUtcForOracle(new Date(startDate)), DateUtils.toUtcForOracle(new Date(endDate)), days);
        List<Object[]> resultOpenSeat = repository.countOpenSeatInRangeByStaff(
                staffId, DateUtils.toUtcForOracle(new Date(startDate)), DateUtils.toUtcForOracle(new Date(endDate)), days);
        List<CountVacancySeat> items = new ArrayList<>();
        Stream.concat(resultClosedSeat.stream().map(
                item -> new CountVacancySeat((Date) item[0], ((BigDecimal) item[1]).intValue(), 0)),
                resultOpenSeat.stream().map(
                        item -> new CountVacancySeat((Date) item[0], 0, ((BigDecimal) item[1]).intValue())))
                .collect(Collectors.groupingBy(CountVacancySeat::getCountOnDate)).forEach((key, value) -> {
            int closedSeat = value.stream().mapToInt(CountVacancySeat::getClosedSeats).sum();
            int openSeat = value.stream().mapToInt(CountVacancySeat::getOpenSeats).sum();
            items.add(new CountVacancySeat(key, closedSeat, openSeat));
        });
        return items;
    }

    @Override
    public void deleteAllByVacancyId(List<Long> vacancyIds) {
        repository.deleteAllByVacancyId(vacancyIds);
    }
}

