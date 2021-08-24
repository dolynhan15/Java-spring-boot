/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qooco.boost.data.oracle.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author mhvtrung
 */
@Entity
@Table(name = "WORKING_HOUR")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "WorkingHour.findByWorkingType", query = "SELECT w FROM WorkingHour w WHERE w.workingType = :workingType")})
public class WorkingHour implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WORKING_HOUR_SEQUENCE")
    @SequenceGenerator(sequenceName = "WORKING_HOUR_SEQ", allocationSize = 1, name = "WORKING_HOUR_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "WORKING_HOUR_ID")
    private Long workingHourId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "WORKING_HOUR_DESCRIPTION", columnDefinition = "NVARCHAR2")
    private String workingHourDescription;
    @Basic(optional = false)
    @NotNull
    @Column(name = "START_TIME")
    private String startDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "END_TIME")
    private String endDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "WORKING_TYPE")
    private boolean workingType;

    public WorkingHour() {
    }

    public WorkingHour(Long workingHourId) {
        this.workingHourId = workingHourId;
    }

    public WorkingHour(Long workingHourId, String workingHourDescription,
                       String startDate, String endDate, boolean workingType) {
        this.workingHourId = workingHourId;
        this.workingHourDescription = workingHourDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.workingType = workingType;
    }

    public Long getWorkingHourId() {
        return workingHourId;
    }

    public void setWorkingHourId(Long workingHourId) {
        this.workingHourId = workingHourId;
    }

    public String getWorkingHourDescription() {
        return workingHourDescription;
    }

    public void setWorkingHourDescription(String workingHourDescription) {
        this.workingHourDescription = workingHourDescription;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isWorkingType() {
        return workingType;
    }

    public void setWorkingType(boolean workingType) {
        this.workingType = workingType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workingHourId != null ? workingHourId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkingHour that = (WorkingHour) o;
        return workingHourId.equals(that.workingHourId);
    }

    @Override
    public String toString() {
        return workingHourDescription;
    }

}
