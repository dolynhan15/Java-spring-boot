package com.qooco.boost.data.oracle.entities;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/6/2018 - 11:17 AM
*/
@Entity
@Table(name = "APPOINTMENT_DETAIL")
public class AppointmentDetail extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPOINTMENT_DETAIL_SEQUENCE")
    @SequenceGenerator(sequenceName = "APPOINTMENT_DETAIL_SEQ", allocationSize = 1, name = "APPOINTMENT_DETAIL_SEQUENCE")
//    @Basic(optional = false)
    @NotNull
    @Column(name = "APPOINTMENT_DETAIL_ID")
    private Long id;

    @Setter
    @Getter
    @NotNull
    @JoinColumn(name = "APPOINTMENT_ID", nullable = false, referencedColumnName = "APPOINTMENT_ID")
    @ManyToOne( fetch = FetchType.LAZY)
    private Appointment appointment;

    @Setter
    @Getter
    @JoinColumn(name = "CURRICULUM_VITAE_ID", referencedColumnName = "CURRICULUM_VITAE_ID")
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private UserCurriculumVitae userCurriculumVitae;

    @Setter
    @Getter
    @Column(name = "STATUS")
    private Integer status;

    @Setter
    @Getter
//    @Basic(optional = false)
    @Column(name = "APPOINTMENT_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date appointmentTime;

    @Getter
    @Setter
    @Column(name="APPOINTMENT_ID", nullable=false, updatable=false, insertable=false)
    private long appointmentId;

    public AppointmentDetail() {
        super();
    }

    public AppointmentDetail(Long ownerId) {
        super(ownerId);
    }

    public AppointmentDetail(Long id, Long ownerId) {
        this(ownerId);
        this.id = id;
    }

    public AppointmentDetail(Appointment appointment, UserCurriculumVitae userCurriculumVitae, Integer status, Long ownerId) {
        super(ownerId);
        this.appointment = appointment;
        this.userCurriculumVitae = userCurriculumVitae;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentDetail that = (AppointmentDetail) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
