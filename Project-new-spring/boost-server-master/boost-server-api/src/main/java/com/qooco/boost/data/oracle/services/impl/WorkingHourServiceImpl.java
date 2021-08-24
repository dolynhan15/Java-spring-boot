package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.WorkingHour;
import com.qooco.boost.data.oracle.reposistories.WorkingHourRepository;
import com.qooco.boost.data.oracle.services.WorkingHourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class WorkingHourServiceImpl implements WorkingHourService {

    @Autowired
    private WorkingHourRepository workingHourRepository;

    @Override
    public WorkingHour findById(long workingHourId) {
        return workingHourRepository.findById(workingHourId).orElse(null);
    }

    @Override
    public List<WorkingHour> findByIds(long[] lstWorkingHourId)
    {
        if (lstWorkingHourId == null || lstWorkingHourId.length == 0) {
            return new ArrayList<>();
        }
        return workingHourRepository.findByIds(lstWorkingHourId);
    }

    @Override
    public List<WorkingHour> findByWorkingType(boolean workingType) {
        return workingHourRepository.findByWorkingType(workingType);
    }

    @Override
    public List<WorkingHour> getAll() {
        return Lists.newArrayList(workingHourRepository.findAll(Sort.by(Sort.Direction.ASC, "startDate")));
    }

    @Override
    public Boolean exist(Long[] ids) {
        int size = workingHourRepository.countByIds(ids);
        return size == ids.length;
    }
}
