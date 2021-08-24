package com.qooco.boost.threads.services;

import com.qooco.boost.data.oracle.entities.Appointment;

import java.util.List;

public interface AppointmentActorService {
    Appointment updateLazyValue(Appointment appointment);
    List<Appointment> updateLazyValue(List<Appointment> appointments);
}
