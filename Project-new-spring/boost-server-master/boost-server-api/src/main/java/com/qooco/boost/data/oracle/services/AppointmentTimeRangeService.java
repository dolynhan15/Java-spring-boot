package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.AppointmentTimeRange;

import java.util.List;

public interface AppointmentTimeRangeService {

    AppointmentTimeRange findById(Long id);

    List<AppointmentTimeRange> findByAppointmentId(Long appointmentId);

    AppointmentTimeRange save(AppointmentTimeRange appointment);

    List<AppointmentTimeRange> save(List<AppointmentTimeRange> appointments);

    void deleteByAppointmentId(Long appointmentId);
}
