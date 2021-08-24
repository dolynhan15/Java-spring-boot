package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.AppointmentDateRange;
import com.qooco.boost.data.oracle.reposistories.AppointmentDateRangeRepository;
import com.qooco.boost.data.oracle.services.AppointmentDateRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentDateRangeServiceImpl implements AppointmentDateRangeService {

    @Autowired
    private AppointmentDateRangeRepository repository;

    @Override
    public AppointmentDateRange findById(Long id) {
        return repository.findValidById(id);
    }

    @Override
    public List<AppointmentDateRange> findByAppointmentId(Long appointmentId) {
        return repository.findByAppointmentId(appointmentId);
    }

    @Override
    public AppointmentDateRange save(AppointmentDateRange appointmentDateRange) {
        return repository.save(appointmentDateRange);
    }

    @Override
    public List<AppointmentDateRange> save(List<AppointmentDateRange> appointmentDateRanges) {
        return Lists.newArrayList(repository.saveAll(appointmentDateRanges));
    }

    @Override
    public void deleteByAppointmentId(Long appointmentId) {
        repository.deleteByAppointmentId(appointmentId);
    }
}
