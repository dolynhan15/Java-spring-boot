package com.qooco.boost.data.oracle.services.impl.views;

import com.qooco.boost.data.oracle.reposistories.ViewStatisticVacancySeatRepository;
import com.qooco.boost.data.oracle.services.views.ViewStatisticVacancySeatService;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ViewStatisticVacancySeatServiceImpl implements ViewStatisticVacancySeatService {

    @Autowired
    private ViewStatisticVacancySeatRepository repository;
    @Override
    public List<Long[]> findTopEmployeeByCompany(Long company, Long startDate, Long endDate, int limit) {
        List<Object[]> result = repository.findTopEmployeeByCompany(company, DateUtils.toUtcForOracle(new Date(startDate)), DateUtils.toUtcForOracle(new Date(endDate)), limit);
        return result.stream().map(ListUtil::convertBigDecimalToLong).collect(Collectors.toList());
    }

    @Override
    public int countMaxClosedSeatsInCompany(Long company, Long startDate, Long endDate) {
        List<Long[]> maxClosedSeat = findTopEmployeeByCompany(company, startDate, endDate, 1);
        AtomicInteger count = new AtomicInteger();
        maxClosedSeat.stream().findFirst().ifPresent(it -> count.set(it[1].intValue()));
        return count.get();
    }
}
