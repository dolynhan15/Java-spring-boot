package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Entity
@Table(name = "CURRICULUM_VITAE_JOB")
@XmlRootElement
public class CurriculumVitaeJob extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CURRICULUM_VITAE_JOB_SEQUENCE")
    @SequenceGenerator(sequenceName = "CURRICULUM_VITAE_JOB_SEQ", allocationSize = 1, name = "CURRICULUM_VITAE_JOB_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Setter
    @Getter
    @Column(name = "CURRICULUM_VITAE_JOB_ID")
    private Long curriculumVitaeJobId;

    @Setter
    @Getter
    @JoinColumn(name = "CURRICULUM_VITAE_ID", referencedColumnName = "CURRICULUM_VITAE_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserCurriculumVitae userCurriculumVitae;

    @Setter
    @Getter
    @JoinColumn(name = "JOB_ID", referencedColumnName = "JOB_ID")
    @ManyToOne(optional = false)
    private Job job;

    public CurriculumVitaeJob() {
        super();
    }

    public CurriculumVitaeJob(CurriculumVitaeJob vitaeJob) {
        super(vitaeJob.getCreatedBy());
        this.curriculumVitaeJobId = vitaeJob.getCurriculumVitaeJobId();
        this.userCurriculumVitae = vitaeJob.getUserCurriculumVitae();
        this.job = vitaeJob.getJob();
    }

}
