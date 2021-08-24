package com.qooco.boost.models.request.appointment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.Location;
import com.qooco.boost.data.oracle.entities.Staff;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
public class AppointmentBaseReq {
    @JsonIgnore
    private Long id;
    private Long locationId;
    private Long managerId;
    @Deprecated
    private Date selectedDate;
    private List<Date> appointmentDateRange;
    private List<Date> appointmentTimeRange;
    private int type;

    public AppointmentBaseReq() {
        //1: Personal Appointment
        this.type = 1;
    }

    public Appointment updateEntity(Appointment appointment) {
        appointment.setLocation(new Location(this.locationId));
        appointment.setManager(new Staff(this.managerId));
        appointment.setAppointmentDate(this.selectedDate);
        return appointment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentBaseReq req = (AppointmentBaseReq) o;
        return Objects.equals(managerId, req.managerId)
                && Objects.equals(locationId, req.locationId)
                && Objects.equals(type, req.type)
                && appointmentDateRange.equals(req.appointmentDateRange)
                && appointmentTimeRange.equals(req.appointmentTimeRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId, managerId, selectedDate);
    }
}
