package com.qooco.boost.data.oracle.entities;


import com.qooco.boost.utils.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "APPOINTMENT")
public class Appointment extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPOINTMENT_SEQUENCE")
    @SequenceGenerator(sequenceName = "APPOINTMENT_SEQ", allocationSize = 1, name = "APPOINTMENT_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "APPOINTMENT_ID")
    private Long id;

    @Setter
    @Getter
    @Basic(optional = false)
//    @NotNull
    @JoinColumn(name = "LOCATION_ID", nullable = false, referencedColumnName = "LOCATION_ID")
    @ManyToOne(optional = false)
    private Location location;

    @Setter
    @Getter
    @Basic(optional = false)
    @JoinColumn(name = "STAFF_ID", nullable = false, referencedColumnName = "STAFF_ID")
    @ManyToOne(optional = false)
    private Staff manager;

    @Setter
    @Getter
    @Basic(optional = false)
    @JoinColumn(name = "VACANCY_ID", nullable = false, referencedColumnName = "VACANCY_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Vacancy vacancy;

    @Setter
    @Getter
    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AppointmentDateRange> appointmentDateRange;

    @Setter
    @Getter
    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AppointmentTimeRange> appointmentTimeRange;

    @Setter
    @Getter
    @Basic(optional = false)
    @Column(name = "TYPE")
    @ApiModelProperty("1: Personal Appointment, 2: Online Appointment")
    private int type = 1;

    @Setter
    @Getter
    @Basic(optional = false)
    @Column(name = "APPOINTMENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date appointmentDate;

    @Setter
    @Getter
    @Basic(optional = false)
    @Column(name = "FROM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDate;

    public Appointment() {
        super();
    }

    public Appointment(Long createdBy) {
        super(createdBy);
    }

    public Appointment(Long id, Long createdBy) {
        super(createdBy);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public void setDateRangeTimeRange(List<AppointmentDateRange> appointmentDateRange, List<AppointmentTimeRange> appointmentTimeRange) {
        setAppointmentDateRange(appointmentDateRange);
        setAppointmentTimeRange(appointmentTimeRange);
        if (CollectionUtils.isNotEmpty(appointmentDateRange)) {
            fromDate = appointmentDateRange.get(0).getAppointmentDate();
            appointmentDate = getToDateOfAppointment();
        }
    }
    public Date getFromDateOfAppointment() {
        if (CollectionUtils.isNotEmpty(appointmentDateRange)) {
            return appointmentDateRange.stream().map(AppointmentDateRange::getAppointmentDate).max(Date::compareTo).orElse(null);
        }
        return null;
    }

    public Date getToDateOfAppointment() {
        if (CollectionUtils.isNotEmpty(appointmentDateRange)) {
            Date maxDate = appointmentDateRange.stream().map(AppointmentDateRange::getAppointmentDate).max(Date::compareTo).orElse(null);
            if (Objects.nonNull(maxDate) && CollectionUtils.isNotEmpty(appointmentTimeRange)) {
                Date maxHourDate = appointmentTimeRange.stream().map(AppointmentTimeRange::getAppointmentTime).max(Date::compareTo).orElse(null);
                if (Objects.nonNull(maxHourDate)) {
                    maxDate = DateUtils.setHourByTimeStamp(getFromDateOfAppointment(), maxHourDate);
                    if (maxDate.before(maxHourDate)) {
                        return DateUtils.atEndOfHour(DateUtils.addDays(maxDate, 1));
                    }
                    return DateUtils.atEndOfHour(maxDate);
                }
                return DateUtils.atEndOfHour(maxDate);
            }
        }
        return null;
    }
}
