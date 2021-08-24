package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tnlong
 */
@Entity
@Table(name = "ASSESSMENT_LEVEL")
public class AssessmentLevel extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter @Getter
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ASSESSMENT_LEVEL_SEQUENCE")
    @SequenceGenerator(sequenceName = "ASSESSMENT_LEVEL_SEQ", allocationSize = 1, name = "ASSESSMENT_LEVEL_SEQUENCE")
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ASSESSMENT_LEVEL_ID")
    private Long id;

    @Setter @Getter
    @Basic(optional = false)
    @Size(min = 1, max = 20)
    @Column(name = "SCALE_ID", columnDefinition = "NVARCHAR2")
    private String scaleId;

    @Setter @Getter
    @Basic(optional = false)
    @Size(min = 1, max = 20)
    @Column(name = "MAPPING_ID", columnDefinition = "NVARCHAR2")
    private String mappingId;

    @Setter @Getter
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "ASSESSMENT_LEVEL_NAME", columnDefinition = "NVARCHAR2")
    private String levelName;

    @Setter @Getter
    @Basic(optional = false)
    @Size(min = 1, max = 20)
    @Column(name = "ASSESSMENT_LEVEL_DESC", columnDefinition = "NVARCHAR2")
    private String levelDescription;

    @Setter @Getter
    @Basic(optional = false)
    @NotNull
    @Column(name = "ASSESSMENT_LEVEL_VALUE")
    private String assessmentLevel;

    @Setter @Getter
    @JoinColumn(name = "ASSESSMENT_ID", referencedColumnName = "ASSESSMENT_ID")
    @ManyToOne()
    private Assessment assessment;

    @Setter @Getter
    @Basic(optional = false)
    @Column(name = "LEVEL_VALUE")
    private int level;

    public AssessmentLevel() {
        super();
    }

    public AssessmentLevel(Long assessmentLevelId) {
        this();
        this.id = assessmentLevelId;
    }

    public AssessmentLevel(String scaleId, String mappingId, String levelName, String levelDescription, String assessmentLevel) {
        this();
        this.scaleId = scaleId;
        this.mappingId = mappingId;
        this.levelName = levelName;
        this.levelDescription = levelDescription;
        this.assessmentLevel = assessmentLevel;
    }

    public AssessmentLevel(Long createdBy, String scaleId, String mappingId, String levelName, String levelDescription,
                           String assessmentLevel, Assessment assessment) {
        super(createdBy);
        this.scaleId = scaleId;
        this.mappingId = mappingId;
        this.levelName = levelName;
        this.levelDescription = levelDescription;
        this.assessmentLevel = assessmentLevel;
        this.assessment = assessment;
    }

    public AssessmentLevel(Date createdDate, Long createdBy, Date updatedDate, Long updatedBy, boolean isDeleted,
                           String scaleId, String mappingId, String levelName, String levelDescription, String assessmentLevel) {
        super(createdDate, createdBy, updatedDate, updatedBy, isDeleted);
        this.scaleId = scaleId;
        this.mappingId = mappingId;
        this.levelName = levelName;
        this.levelDescription = levelDescription;
        this.assessmentLevel = assessmentLevel;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessmentLevel that = (AssessmentLevel) o;
        return id.equals(that.id);
    }

    @Override
    public String toString() {
        return levelName;
    }

}
