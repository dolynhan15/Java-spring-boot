package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import com.qooco.boost.data.mongo.embedded.message.AppointmentDetailMessage;
import com.qooco.boost.models.dto.LocationDTO;
import com.qooco.boost.models.dto.appointment.ManagerDTO;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentMessageDTO {
    private Long id;
    private ManagerDTO manager;
    private LocationDTO location;
    private List<Date> dateRanges;
    private List<Date> timeRanges;
    private int type;
    private VacancyShortInformationDTO vacancy;
    private Date selectedDate;
    private AppointmentDetailMessageDTO event;

    public AppointmentMessageDTO(AppointmentDetailMessage appointmentDetailMessage, String locale) {
        if (Objects.nonNull(appointmentDetailMessage)) {
            AppointmentEmbedded appointmentEmbedded = appointmentDetailMessage.getAppointment();
            this.id = appointmentEmbedded.getId();
            if (Objects.nonNull(appointmentEmbedded.getManager())) {
                this.manager = new ManagerDTO(appointmentEmbedded.getManager());
            }
            if (Objects.nonNull(appointmentEmbedded.getLocation())) {
                this.location = new LocationDTO(appointmentEmbedded.getLocation(), locale);
            }
            if (Objects.nonNull(appointmentDetailMessage.getVacancy())) {
                this.vacancy = new VacancyShortInformationDTO(appointmentDetailMessage.getVacancy(), locale);
            }
            if (CollectionUtils.isNotEmpty(appointmentEmbedded.getDateRanges())) {
                this.dateRanges = appointmentEmbedded.getDateRanges();
                Collections.sort(this.dateRanges);
            }
            if (CollectionUtils.isNotEmpty(appointmentEmbedded.getTimeRanges())) {
                this.timeRanges = appointmentEmbedded.getTimeRanges();
                Collections.sort(this.timeRanges);
            }
            this.type = appointmentEmbedded.getType();
            this.selectedDate = appointmentEmbedded.getAppointmentDate();
            this.event = new AppointmentDetailMessageDTO(appointmentDetailMessage);
        }
    }
}
