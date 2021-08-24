package com.qooco.boost.models.request.appointment;

import com.qooco.boost.data.oracle.entities.Appointment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 11/26/2018 - 4:08 PM
 */

@Setter @Getter @NoArgsConstructor
public class AppointmentVacancyReq extends AppointmentBaseReq {
    private Long id;

    public Appointment updateEntity(Appointment appointment){
        appointment = super.updateEntity(appointment);
        appointment.setId(id);
        return appointment;
    }
}
