package com.qooco.boost.models.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Setter @Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentDTO extends AppointmentShortDTO {
    private List<AppointmentDetailDTO> events;

    public AppointmentDTO(Appointment appointment, String locale) {
        super(appointment, locale);
    }

    public AppointmentDTO(List<AppointmentDetail> appointmentDetails, String locale) {
        super(appointmentDetails, locale);
        if (CollectionUtils.isNotEmpty(appointmentDetails)) {
            events = appointmentDetails.stream().filter(ap -> !ap.getAppointment().getIsDeleted())
                    .map(it -> new AppointmentDetailDTO(it, locale)).collect(Collectors.toList());
        }
    }

    public AppointmentDTO(AppointmentEmbedded appointment, String locale) {
        super(appointment, locale);
    }
}
