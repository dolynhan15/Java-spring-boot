package com.qooco.boost.data.oracle.entities;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "APPOINTMENT_TIME_RANGE")
public class AppointmentTimeRange extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPOINTMENT_TIME_RANGE_SEQUENCE")
    @SequenceGenerator(sequenceName = "APPOINTMENT_TIME_RANGE_SEQ", allocationSize = 1, name = "APPOINTMENT_TIME_RANGE_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Long id;

    @Setter
    @Getter
    @NotNull
    @JoinColumn(name = "APPOINTMENT_ID", nullable = false, referencedColumnName = "APPOINTMENT_ID")
    @ManyToOne( fetch = FetchType.LAZY)
    private Appointment appointment;

    @Setter
    @Getter
    @Basic(optional = false)
    @Column(name = "APPOINTMENT_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date appointmentTime;

    public AppointmentTimeRange() {
        super();
    }

    public AppointmentTimeRange(Long createdBy) {
        super(createdBy);
    }

    public AppointmentTimeRange(Date appointmentTime, Appointment appointment) {
        this();
        this.appointmentTime = appointmentTime;
        this.appointment = appointment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentTimeRange that = (AppointmentTimeRange) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
