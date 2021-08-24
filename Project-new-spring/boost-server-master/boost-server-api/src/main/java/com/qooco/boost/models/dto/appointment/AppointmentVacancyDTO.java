package com.qooco.boost.models.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.models.dto.LocationDTO;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentVacancyDTO {
    private Long id;
    private ManagerDTO manager;
    private LocationDTO location;
    private Date selectedDate;
    private List<Date> dateRanges;
    private List<Date> timeRanges;
    private int type;

    @Deprecated
    private List<Date> appointmentDateRange;
    @Deprecated
    private List<Date> appointmentTimeRange;

    public AppointmentVacancyDTO(Appointment appointment, String locale) {
        this.id = appointment.getId();
        this.manager = new ManagerDTO(appointment.getManager());
        this.location = new LocationDTO(appointment.getLocation(), locale);
        this.selectedDate = DateUtils.getUtcForOracle(appointment.getAppointmentDate());
        if (Objects.nonNull(appointment.getAppointmentDateRange())) {
            this.dateRanges = this.appointmentDateRange = appointment.getAppointmentDateRange().stream()
                    .map(date -> DateUtils.getUtcForOracle(date.getAppointmentDate())).collect(Collectors.toList());
            Collections.sort(this.appointmentDateRange);
        }
        if (Objects.nonNull(appointment.getAppointmentTimeRange())) {
            this.timeRanges = this.appointmentTimeRange = appointment.getAppointmentTimeRange().stream()
                    .map(time -> DateUtils.getUtcForOracle(time.getAppointmentTime())).collect(Collectors.toList());
            Collections.sort(this.appointmentTimeRange);
        }
        this.type = appointment.getType();
    }
}
