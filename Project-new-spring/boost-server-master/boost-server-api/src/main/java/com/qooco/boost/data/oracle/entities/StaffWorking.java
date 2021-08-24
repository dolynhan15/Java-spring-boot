/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qooco.boost.data.oracle.entities;

import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "STAFF_WORKING")
@NoArgsConstructor
public class StaffWorking extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STAFF_WORKING_SEQUENCE")
    @SequenceGenerator(sequenceName = "STAFF_WORKING_SEQ", allocationSize = 1, name = "STAFF_WORKING_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Setter @Getter
    @JoinColumn(name = "STAFF_ID", referencedColumnName = "STAFF_ID")
    @ManyToOne(optional = false)
    private Staff staff;
    @Setter @Getter
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Setter @Getter
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;


    public StaffWorking(@NonNull Staff staff, long startDate, long endDate) {
        super(staff.getUserFit().getUserProfileId());
        this.staff = staff;
        this.startDate = DateUtils.toUtcForOracle(new Date(startDate));
        this.endDate = DateUtils.toUtcForOracle(new Date(endDate));
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaffWorking staffWorking = (StaffWorking) o;
        return Objects.equals(id, staffWorking.id);
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
