package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.AppointmentDateRange;

import java.util.List;

public interface AppointmentDateRangeService {

    AppointmentDateRange findById(Long id);

    List<AppointmentDateRange> findByAppointmentId(Long appointmentId);

    AppointmentDateRange save(AppointmentDateRange appointment);

    List<AppointmentDateRange> save(List<AppointmentDateRange> appointments);

    void deleteByAppointmentId(Long appointmentId);
}
