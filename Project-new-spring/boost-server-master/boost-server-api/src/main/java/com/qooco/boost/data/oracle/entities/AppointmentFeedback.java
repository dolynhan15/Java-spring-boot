/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qooco.boost.data.oracle.entities;

import com.qooco.boost.utils.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "APPOINTMENT_FEEDBACK")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class AppointmentFeedback extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPOINTMENT_FEEDBACK_SEQUENCE")
    @SequenceGenerator(sequenceName = "APPOINTMENT_FEEDBACK_SEQ", allocationSize = 1, name = "APPOINTMENT_FEEDBACK_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Setter @Getter
    @JoinColumn(name = "STAFF_ID", referencedColumnName = "STAFF_ID")
    @ManyToOne(optional = false)
    private Staff staff;

    @Setter @Getter
    @JoinColumn(name = "APPOINTMENT_DETAIL_ID", referencedColumnName = "APPOINTMENT_DETAIL_ID")
    @ManyToOne(optional = false)
    private AppointmentDetail appointmentDetail;

    @Setter @Getter
    @Column(name = "FEEDBACK_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date feedbackDate;

    @Setter @Getter
    @Column(name = "STATUS")
    private int status;


    public AppointmentFeedback(@NonNull Staff staff, @NonNull AppointmentDetail appointmentDetail, long date, int status) {
        super(staff.getUserFit().getUserProfileId());
        this.staff = staff;
        this.appointmentDetail = appointmentDetail;
        this.feedbackDate = DateUtils.toUtcForOracle(new Date(date));
        this.status = status;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentFeedback appointmentFeedback = (AppointmentFeedback) o;
        return Objects.equals(id, appointmentFeedback.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return Objects.nonNull(staff) ? staff.getStaffId().toString() : "";
    }

}
