package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.message.AppointmentDetailMessage;
import com.qooco.boost.data.mongo.embedded.message.MessageStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentDetailMessageDTO extends MessageStatus {
    private Long id;
    private Date eventTime;
    private Integer responseStatus;
    private int appointmentDetailStatus;

    public AppointmentDetailMessageDTO(AppointmentDetailMessage appointmentDetailMessage) {
        this.id = appointmentDetailMessage.getId();
        this.eventTime = appointmentDetailMessage.getAppointmentTime();
        this.responseStatus = appointmentDetailMessage.getResponseStatus();
        this.appointmentDetailStatus = appointmentDetailMessage.getAppointmentDetailStatus();
        this.setStatus(appointmentDetailMessage.getStatus());
    }
}
