package com.qooco.boost.models.request.appointment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 2/25/2019 - 8:56 AM
*/
@Getter
@Setter
@NoArgsConstructor
public class AppointmentCandidate {
    private Long appointmentId;
    private Long userCVId;

    public AppointmentCandidate(long appointmentId, long userCVId) {
        this.appointmentId = appointmentId;
        this.userCVId = userCVId;
    }
}
