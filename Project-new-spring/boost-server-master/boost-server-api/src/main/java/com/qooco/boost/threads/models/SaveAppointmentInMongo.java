package com.qooco.boost.threads.models;

import com.qooco.boost.data.oracle.entities.Appointment;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class SaveAppointmentInMongo {
    private Appointment appointment;
    private Long vacancyId;

    public SaveAppointmentInMongo(Appointment appointment, Long vacancyId) {
        if (Objects.nonNull(appointment)) {
            this.appointment = appointment;
        }
        this.vacancyId = vacancyId;
    }
}
