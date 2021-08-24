package com.qooco.boost.threads.models;

import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SaveAppointmentDetailInMongo {
    Appointment appointment;
    List<AppointmentDetail> events;
}
