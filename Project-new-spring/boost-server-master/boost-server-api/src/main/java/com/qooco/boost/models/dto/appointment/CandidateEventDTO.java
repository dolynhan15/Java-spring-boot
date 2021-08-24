package com.qooco.boost.models.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.models.dto.message.ConversationDTO;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateEventDTO {
    private Long id;
    private AppointmentShortDTO appointment;
    private Date eventTime;
    private int status;

    private ConversationDTO conversation;

    public CandidateEventDTO(AppointmentDetail appointmentDetail, ConversationDoc conversationDoc, String token, String locale) {
        this.id = appointmentDetail.getId();
        this.appointment = new AppointmentShortDTO(appointmentDetail.getAppointment(), locale);
        this.eventTime = DateUtils.getUtcForOracle(appointmentDetail.getAppointmentTime());
        this.status = AppointmentStatus.getStatusValue(appointmentDetail.getStatus());
        if (Objects.nonNull(conversationDoc)) {
            this.conversation = new ConversationDTO(conversationDoc, token, locale);
        }
    }
}
