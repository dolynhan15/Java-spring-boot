package com.qooco.boost.models.request.appointment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class CandidateEvent {
    private Long id;
    private Long userCVId;
    @JsonIgnore
    private Long appointmentId;
    private Date eventTime;
    @ApiModelProperty(notes = "ADD :1 , MODIFY: 2, DELETE: 3")
    private int eventAction;

    public CandidateEvent(Long userCVId, Date eventTime) {
        this.userCVId = userCVId;
        this.eventTime = eventTime;
    }

    public CandidateEvent(CandidateEvent event) {
        this.userCVId = event.getUserCVId();
        this.eventTime = event.getEventTime();
        this.eventAction = event.getEventAction();
        this.appointmentId = event.getAppointmentId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandidateEvent that = (CandidateEvent) o;
        return Objects.equals(userCVId, that.userCVId) &&
                Objects.equals(eventTime, that.eventTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userCVId, eventTime);
    }
}
