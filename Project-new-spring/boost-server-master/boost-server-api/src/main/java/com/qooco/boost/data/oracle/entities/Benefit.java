package com.qooco.boost.data.oracle.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author mhvtrung
 */
@Entity
@Table(name = "BENEFIT")
public class Benefit extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BENEFIT_SEQUENCE")
    @SequenceGenerator(sequenceName = "BENEFIT_SEQ", allocationSize = 1, name = "BENEFIT_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "BENEFIT_ID")
    private Long benefitId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "DESCRIPTION")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "NAME")
    private String name;

    public Benefit() {
        super();
    }

    public Benefit(Long benefitId) {
        super();
        this.benefitId = benefitId;
    }

    public Benefit(Long benefitId, String description, String name, Long createdBy, Date createdDate, Long updatedBy, Date updatedDate, boolean isDeleted) {
        super(createdDate, createdBy, updatedDate, updatedBy, isDeleted);
        this.benefitId = benefitId;
        this.description = description;
        this.name = name;
    }

    public Long getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(Long benefitId) {
        this.benefitId = benefitId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (benefitId != null ? benefitId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Benefit benefit = (Benefit) o;
        return benefitId.equals(benefit.benefitId);
    }

    @Override
    public String toString() {
        return "OK.Benefit[ benefitId=" + benefitId + " ]";
    }

}
