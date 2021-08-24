package com.qooco.boost.data.oracle.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 7/3/2018 - 1:41 PM
 */
@Entity
@Table(name = "SOFT_SKILL")
public class SoftSkill extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOFT_SKILL_SEQUENCE")
    @SequenceGenerator(sequenceName = "SOFT_SKILL_SEQ", allocationSize = 1, name = "SOFT_SKILL_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "SOFT_SKILL_ID")
    private Long softSkillId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "NAME")
    private String name;

    @Size(max = 20)
    @Column(name = "DESCRIPTION")
    private String description;

    public SoftSkill() {
    }

    public SoftSkill(Long softSkillId) {
        this.softSkillId = softSkillId;
    }

    public SoftSkill(Long softSkillId, String name) {
        this.softSkillId = softSkillId;
        this.name = name;
    }

    public Long getSoftSkillId() {
        return softSkillId;
    }

    public void setSoftSkillId(Long softSkillId) {
        this.softSkillId = softSkillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoftSkill softSkill = (SoftSkill) o;
        return Objects.equals(softSkillId, softSkill.softSkillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(softSkillId);
    }

    @Override
    public String toString() {
        return name;
    }

}
