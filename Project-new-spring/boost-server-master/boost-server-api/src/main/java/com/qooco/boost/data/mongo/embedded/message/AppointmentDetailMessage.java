package com.qooco.boost.data.mongo.embedded.message;

import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentDetailMessage extends MessageStatus{
    private Long id;
    private AppointmentEmbedded appointment;
    private VacancyEmbedded vacancy;
    private Date appointmentTime;
    @ApiModelProperty(notes = "Accept " + MessageConstants.APPOINTMENT_STATUS_ACCEPTED + " Decline " + MessageConstants.APPOINTMENT_STATUS_DECLINED)
    private Integer responseStatus;
    private int appointmentDetailStatus;

    public AppointmentDetailMessage(AppointmentDetailMessage appointmentDetailMessage) {
        super(appointmentDetailMessage);
        if (Objects.nonNull(appointmentDetailMessage)) {
            id = appointmentDetailMessage.getId();
            if (Objects.nonNull(appointmentDetailMessage.getAppointment())) {
                appointment = new AppointmentEmbedded(appointmentDetailMessage.getAppointment());
            }
            if (Objects.nonNull(appointmentDetailMessage.getVacancy())) {
                vacancy = new VacancyEmbedded(appointmentDetailMessage.getVacancy());
            }
            appointmentTime = appointmentDetailMessage.getAppointmentTime();
            responseStatus = appointmentDetailMessage.getResponseStatus();
            appointmentDetailStatus = appointmentDetailMessage.getAppointmentDetailStatus();
        }
    }
}
