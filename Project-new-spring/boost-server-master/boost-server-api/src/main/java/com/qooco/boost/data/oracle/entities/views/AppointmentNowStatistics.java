package com.qooco.boost.data.oracle.entities.views;

import com.qooco.boost.data.oracle.entities.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 4/10/2019 - 1:35 PM
*/
@Entity
@Table(name = "APPOINTMENT_NOW_STATISTICS")
@XmlRootElement
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class AppointmentNowStatistics implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPOINTMENT_SEQUENCE")
    @SequenceGenerator(sequenceName = "APPOINTMENT_SEQ", allocationSize = 1, name = "APPOINTMENT_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "APPOINTMENT_ID")
    private Long id;

    @Basic(optional = false)
    @JoinColumn(name = "LOCATION_ID", nullable = false, referencedColumnName = "LOCATION_ID")
    @ManyToOne(optional = false)
    private Location location;

    @Basic(optional = false)
    @JoinColumn(name = "STAFF_ID", nullable = false, referencedColumnName = "STAFF_ID")
    @ManyToOne(optional = false)
    private Staff manager;

    @Basic(optional = false)
    @JoinColumn(name = "VACANCY_ID", nullable = false, referencedColumnName = "VACANCY_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Vacancy vacancy;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AppointmentDateRange> appointmentDateRange;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AppointmentTimeRange> appointmentTimeRange;

    @Basic(optional = false)
    @Column(name = "TYPE")
    @ApiModelProperty("1: Personal Appointment, 2: Online Appointment")
    private int type = 1;

    @Basic(optional = false)
    @Column(name = "APPOINTMENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date appointmentDate;

    @Basic(optional = false)
    @Column(name = "FROM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDate;

    @Column(name = "NEXT_PENDING_APPOINTMENT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextPendingAppointment;

    @Column(name = "NEXT_ACCEPTED_APPOINTMENT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextAcceptedAppointment;

    @Column(name = "LAST_ACCEPTED_APPOINTMENT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAcceptedAppointment;

    @Column(name = "COUNT_TOTAL")
    private int countTotal;

    @Column(name = "ACCEPTED_EVENTS")
    private int acceptedEvents;

    @Column(name = "NEXT_ACCEPTED_EVENTS")
    private int nextAcceptedEvents;

    @Column(name = "NEXT_PENDING_EVENTS")
    private int nextPendingEvents;

    @Column(name = "CANCELLED_EVENTS")
    private int cancelledEvents;

    @Column(name = "CHANGED_EVENTS")
    private int changedEvents;

    @Column(name = "CANCELLED_CHANGED_EVENTS")
    private int cancelledChangedEvents;

    @Column(name = "DECLINED_EVENTS")
    private int declinedEvents;

    @Column(name = "PENDING_EVENTS")
    private int pendingEvents;
}
