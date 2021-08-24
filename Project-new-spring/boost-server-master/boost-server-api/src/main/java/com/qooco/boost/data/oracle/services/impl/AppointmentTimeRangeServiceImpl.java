package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.AppointmentTimeRange;
import com.qooco.boost.data.oracle.reposistories.AppointmentTimeRangeRepository;
import com.qooco.boost.data.oracle.services.AppointmentTimeRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentTimeRangeServiceImpl implements AppointmentTimeRangeService {

    @Autowired
    private AppointmentTimeRangeRepository repository;

    @Override
    public AppointmentTimeRange findById(Long id) {
        return repository.findValidById(id);
    }

    @Override
    public List<AppointmentTimeRange> findByAppointmentId(Long appointmentId) {
        return repository.findByAppointmentId(appointmentId);
    }

    @Override
    public AppointmentTimeRange save(AppointmentTimeRange appointmentDateRange) {
        return repository.save(appointmentDateRange);
    }

    @Override
    public List<AppointmentTimeRange> save(List<AppointmentTimeRange> appointmentDateRanges) {
        return Lists.newArrayList(repository.saveAll(appointmentDateRanges));
    }

    @Override
    public void deleteByAppointmentId(Long appointmentId) {
        repository.deleteByAppointmentId(appointmentId);
    }
}
