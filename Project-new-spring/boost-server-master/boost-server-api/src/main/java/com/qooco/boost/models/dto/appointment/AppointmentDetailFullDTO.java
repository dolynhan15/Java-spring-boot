package com.qooco.boost.models.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentDetailFullDTO extends AppointmentDetailDTO {
    @Getter
    @Setter
    private AppointmentShortDTO appointment;

    public AppointmentDetailFullDTO(AppointmentDetail appointmentDetail, String locale) {
        super(appointmentDetail, locale);
        appointment = new AppointmentShortDTO(appointmentDetail.getAppointment(), locale);
    }
}
