package com.qooco.boost.models.sdo;

import com.qooco.boost.data.mongo.embedded.appointment.AppointmentSlotEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class AppointmentSlotEmbeddedSDO {
    private long userCvId;
    private AppointmentSlotEmbedded appointmentSlotEmbedded;
}
