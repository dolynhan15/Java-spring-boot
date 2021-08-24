package com.qooco.boost.data.oracle.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "USER_QUALIFICATION")
@Setter
@Getter
@FieldNameConstants
public class UserQualification extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_QUALIFICATION_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_QUALIFICATION_SEQ", allocationSize = 1, name = "USER_QUALIFICATION_SEQUENCE")
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "QUALIFICATION_ID")
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "ASSESSMENT_ID")
    private Long assessmentId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "USER_PROFILE_ID")
    private Long userProfileId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "SCALE_ID", columnDefinition = "NVARCHAR2")
    private String scaleId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "NAME", columnDefinition = "NVARCHAR2")
    private String assessmentName;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "LEVEL_NAME", columnDefinition = "NVARCHAR2")
    private String levelName;

    @Column(name = "LEVEL_VALUE")
    private Integer levelValue;

    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "EXPIRED_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredTime;

    @Basic(optional = false)
    @Column(name = "SUBMISSION_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date submissionTime;
    @JoinColumn(name = "ASSESSMENT_ID", nullable = false, referencedColumnName = "ASSESSMENT_ID", insertable = false, updatable = false)
    @ManyToOne(cascade = CascadeType.REFRESH)
    private Assessment assessment;

    public UserQualification() {
        super();
    }

    public UserQualification(Long id) {
        this.id = id;
    }

    public UserQualification(Long id, String scaleId, Long assessmentId) {
        this(id);
        this.scaleId = scaleId;
        this.assessmentId = assessmentId;
    }

    public UserQualification(UserQualification qualification) {
        this(qualification.getId());
        this.scaleId = qualification.getScaleId();
        this.assessmentId = qualification.getAssessmentId();
        this.userProfileId = qualification.getUserProfileId();
        this.assessmentName = qualification.getAssessmentName();
        this.levelName = qualification.getLevelName();
        this.levelValue = qualification.getLevelValue();
        this.expiredTime = qualification.getExpiredTime();
        this.submissionTime = qualification.getSubmissionTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserQualification that = (UserQualification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return assessmentName;
    }
}