package com.qooco.boost.data.oracle.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author mhvtrung
 */
@Entity
@Table(name = "JOB")
public class Job extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "JOB_SEQUENCE")
    @SequenceGenerator(sequenceName = "JOB_SEQ", allocationSize = 1, name = "JOB_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "JOB_ID")
    private Long jobId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "JOB_NAME", columnDefinition = "NVARCHAR2")
    private String jobName;

    @Basic(optional = false)
    @Size(min = 1, max = 1000)
    @Column(name = "JOB_DESCRIPTION", columnDefinition = "NVARCHAR2")
    private String jobDescription;

    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID")
    @ManyToOne()
    private Company company;
    
    public Job() {
        super();
    }

    public Job(Long jobId) {
        super();
        this.jobId = jobId;
    }

    public Job(String jobName) {
        this.jobName = jobName;
    }

    public Job(String jobName, Long companyId) {
        this.jobName = jobName;
        this.company= new Company(companyId);
    }

    public Job(Long jobId, String jobName, String jobDescription, Long createdBy, Date createdDate, Long updatedBy, Date updatedDate, boolean isDeleted) {
        super(createdDate, createdBy, updatedDate, updatedBy, isDeleted);
        this.jobId = jobId;
        this.jobName = jobName;
        this.jobDescription = jobDescription;
    }

    public Job(Job job) {
        if (Objects.nonNull(job)) {
            jobId = job.getJobId();
            jobName = job.getJobName();
            jobDescription = job.getJobDescription();
            if (Objects.nonNull(job.getCompany())) {
                company = new Company(job.getCompany());
            }
        }
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (jobId != null ? jobId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(jobId, job.jobId);
    }

    @Override
    public String toString() {
        return jobName;
    }

}
