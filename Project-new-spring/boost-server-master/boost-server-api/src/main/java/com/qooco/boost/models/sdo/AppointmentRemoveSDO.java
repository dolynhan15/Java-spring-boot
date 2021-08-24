package com.qooco.boost.models.sdo;

import com.qooco.boost.data.oracle.entities.Appointment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AppointmentRemoveSDO {
    private Long deleteId;
    private Appointment appointment;

    public AppointmentRemoveSDO(Long deleteId, Appointment appointment) {
        this.deleteId = deleteId;
        this.appointment = appointment;
    }
}
