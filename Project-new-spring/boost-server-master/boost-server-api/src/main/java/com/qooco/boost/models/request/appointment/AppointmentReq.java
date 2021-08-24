package com.qooco.boost.models.request.appointment;

import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.Vacancy;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class AppointmentReq extends AppointmentBaseReq {
    private Long vacancyId;

    public AppointmentReq() {
        super();
    }

    public Appointment updateEntity(Appointment appointment){
        appointment = super.updateEntity(appointment);
        appointment.setVacancy(new Vacancy(this.vacancyId));
        return appointment;
    }
}
