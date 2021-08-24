package com.qooco.boost.data.mongo.embedded.appointment;

import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentSlotEmbedded {
    private Long id;
    private Long appointmentId;
    private int status;
    private Date appointmentTime;
    private Date expiredDate;

    public AppointmentSlotEmbedded(Long id) {
        this.id = id;
    }

    public AppointmentSlotEmbedded(AppointmentDetailDoc doc) {
        this.id = doc.getId();
        this.status = doc.getStatus();
        this.appointmentId = doc.getAppointment().getId();
        this.appointmentTime = doc.getAppointmentTime();
        this.expiredDate = doc.getAppointment().getToDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentSlotEmbedded that = (AppointmentSlotEmbedded) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}