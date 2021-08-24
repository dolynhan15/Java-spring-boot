package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.mongo.embedded.message.AppointmentDetailMessage;
import com.qooco.boost.models.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class AppointmentDTO {
    private Long id;
    private CompanyDTO company;
    private LocationDTO location;


    public AppointmentDTO(AppointmentDetailMessage embedded, String locale) {
        if (Objects.nonNull(embedded)) {
            this.id = embedded.getAppointment().getId();
            this.company = new CompanyDTO(embedded.getVacancy().getCompany());
            this.location = new LocationDTO(embedded.getAppointment().getLocation(), locale);
        }
    }
}
