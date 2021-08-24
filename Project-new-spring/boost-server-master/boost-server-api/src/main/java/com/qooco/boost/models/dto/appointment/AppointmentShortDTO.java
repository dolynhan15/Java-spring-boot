package com.qooco.boost.models.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.models.dto.BaseDTO;
import com.qooco.boost.models.dto.LocationDTO;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import com.qooco.boost.utils.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter @Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentShortDTO extends BaseDTO {
    private Long id;
    private ManagerDTO manager;
    private LocationDTO location;
    private VacancyShortInformationDTO vacancy;
    private List<Date> dateRanges;
    private List<Date> timeRanges;

    private int type;
    private Date selectedDate;

    @Deprecated
    private List<Date> appointmentDateRange;
    @Deprecated
    private List<Date> appointmentTimeRange;

    @ApiModelProperty(notes = "NONE(0), LOCKED_EDIT(1), LOCKED_DELETE(2)")
    private int lockedStatus;

    public AppointmentShortDTO(Appointment appointment, String locale) {
        if (Objects.nonNull(appointment)) {
            this.id = appointment.getId();
            if (Objects.nonNull(appointment.getManager())) {
                this.manager = new ManagerDTO(appointment.getManager());
            }
            if (Objects.nonNull(appointment.getLocation())) {
                this.location = new LocationDTO(appointment.getLocation(), locale);
            }
            if (Objects.nonNull(appointment.getVacancy())) {
                this.vacancy = new VacancyShortInformationDTO(appointment.getVacancy(), locale);
            }
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
            this.selectedDate = DateUtils.getUtcForOracle(appointment.getAppointmentDate());
            this.setUpdatedDate(DateUtils.getUtcForOracle(appointment.getUpdatedDate()));
        }
    }

    public AppointmentShortDTO(List<AppointmentDetail> appointmentDetails, String locale) {
        if (CollectionUtils.isNotEmpty(appointmentDetails)) {
            Appointment appointment = appointmentDetails.get(0).getAppointment();
            if (Objects.nonNull(appointment)) {
                this.selectedDate = DateUtils.getUtcForOracle(appointment.getAppointmentDate());
                this.id = appointment.getId();
                this.manager = new ManagerDTO(appointment.getManager());
                this.vacancy = new VacancyShortInformationDTO(appointment.getVacancy(), locale);
                this.location = new LocationDTO(appointment.getLocation(), locale);
                if (Objects.nonNull(appointment.getAppointmentDateRange())) {
                    this.appointmentDateRange = appointment.getAppointmentDateRange().stream()
                            .map(date -> DateUtils.getUtcForOracle(date.getAppointmentDate())).collect(Collectors.toList());
                }
                if (Objects.nonNull(appointment.getAppointmentTimeRange())) {
                    this.appointmentTimeRange = appointment.getAppointmentTimeRange().stream()
                            .map(time -> DateUtils.getUtcForOracle(time.getAppointmentTime())).collect(Collectors.toList());
                }
                this.type = appointment.getType();
            }
        }
    }

    public AppointmentShortDTO(AppointmentEmbedded appointment, String locale) {
        if (Objects.nonNull(appointment)) {
            this.id = appointment.getId();
            if (Objects.nonNull(appointment.getManager())) {
                this.manager = new ManagerDTO(appointment.getManager());
            }
            if (Objects.nonNull(appointment.getLocation())) {
                this.location = new LocationDTO(appointment.getLocation(), locale);
            }
            if (Objects.nonNull(appointment.getDateRanges())) {
                this.appointmentDateRange = appointment.getDateRanges().stream()
                        .map(DateUtils::getUtcForOracle).collect(Collectors.toList());
            }
            if (Objects.nonNull(appointment.getTimeRanges())) {
                this.appointmentTimeRange = appointment.getTimeRanges().stream()
                        .map(DateUtils::getUtcForOracle).collect(Collectors.toList());
            }
            this.type = appointment.getType();
            this.selectedDate = appointment.getAppointmentDate();
        }
    }
}
