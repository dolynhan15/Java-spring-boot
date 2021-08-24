package com.qooco.boost.models.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/30/2018 - 2:13 PM
*/
@Setter @Getter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentShortVacancyDTO extends AppointmentDetailDTO {

    private VacancyShortInformationDTO vacancy;

    public AppointmentShortVacancyDTO(AppointmentDetail appointmentDetail, boolean isLocked, String locale) {
        super(appointmentDetail, isLocked, locale);
        if (Objects.nonNull(appointmentDetail.getAppointment())
                && Objects.nonNull(appointmentDetail.getAppointment().getVacancy())) {
            this.vacancy = new VacancyShortInformationDTO(appointmentDetail.getAppointment().getVacancy(), locale);
        }
    }


}
