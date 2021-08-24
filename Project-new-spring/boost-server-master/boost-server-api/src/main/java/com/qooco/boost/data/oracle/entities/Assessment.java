package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "ASSESSMENT")
@FieldNameConstants
public class Assessment extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ASSESSMENT_ID")
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
    @Column(name = "PACKAGE_ID")
    private Long packageId;

    @Setter @Getter
    @Column(name = "CATEGORY_ID")
    private Long categoryId;

    @Setter @Getter
    @Column(name = "TOPIC_ID")
    private Long topicId;

    @Setter @Getter
    @Basic(optional = false)
    @Size(min = 1, max = 128)
    @Column(name = "NAME")
    private String name;

    @Setter @Getter
    @Basic(optional = false)
    @Column(name = "PRICE", columnDefinition = "NUMBER(12,2)")
    private Double price;

    @Setter @Getter
    @Basic(optional = false)
    @Size(min = 1, max = 1000)
    @Column(name = "PICTURE")
    private String picture;

    @Setter @Getter
    @Basic(optional = false)
    @Column(name = "NUMBER_COMPANY_REQUIRE")
    private int numberCompanyRequire;

    @Setter @Getter
    @Basic(optional = false)
    @Column(name = "TYPE")
    private Integer type;


    @Setter @Getter
    @Column(name = "TIME_LIMIT")
    private Long timeLimit;

    @Setter @Getter
    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AssessmentLevel> assessmentLevels;

    public Assessment() {
        super();
    }

    public Assessment(Long assessmentId) {
        this.id = assessmentId;
    }

    public Assessment(Long assessmentId, String name) {
        this.id = assessmentId;
        this.name = name;
    }

    public Assessment(Long id, String name, Double price, String picture, int numberCompanyRequire,
                      Integer type, Long timeLimit, List<AssessmentLevel> assessmentLevels) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.picture = picture;
        this.numberCompanyRequire = numberCompanyRequire;
        this.type = type;
        this.timeLimit = timeLimit;
        this.assessmentLevels = assessmentLevels;
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
        Assessment that = (Assessment) o;
        return id.equals(that.id);
    }

    @Override
    public String toString() {
        return name;
    }

}
