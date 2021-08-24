package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.mongo.entities.base.MessageBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
public class PushAppointmentDetailMessageDTO extends PushMessageBaseDTO {
    private Long eventId;
    private Date eventTime;
    private AppointmentDTO appointment;

    public PushAppointmentDetailMessageDTO(MessageBase message, String locale) {
        super(message);
        ofNullable(message.getAppointmentDetailMessage()).ifPresent(it -> {
            this.eventId = it.getId();
            this.eventTime = it.getAppointmentTime();
            this.appointment = new AppointmentDTO(it, locale);
        });
    }
}
