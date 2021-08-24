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
import java.util.Objects;

/**
 * @author mhvtrung
 */
@Entity
@Table(name = "EDUCATION")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Education.findAll", query = "SELECT e FROM Education e")
        , @NamedQuery(name = "Education.findByEducationId", query = "SELECT e FROM Education e WHERE e.educationId = :educationId")
        , @NamedQuery(name = "Education.findByName", query = "SELECT e FROM Education e WHERE e.name = :name")
})
public class Education implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EDUCATION_SEQUENCE")
    @SequenceGenerator(sequenceName = "EDUCATION_SEQ", allocationSize = 1, name = "EDUCATION_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "EDUCATION_ID")
    private Long educationId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "NAME", columnDefinition = "NVARCHAR2")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "DESCRIPTION")
    private String description;

    public Education() {
    }

    public Education(Long educationId) {
        this.educationId = educationId;
    }

    public Education(Long educationId, String name, String description) {
        this.educationId = educationId;
        this.name = name;
        this.description = description;
    }

    public Education(Education education) {
        if (Objects.nonNull(education)) {
            this.educationId = education.getEducationId();
            this.name = education.getName();
            this.description = education.getDescription();
        }
    }

    public Long getEducationId() {
        return educationId;
    }

    public void setEducationId(Long educationId) {
        this.educationId = educationId;
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
    public int hashCode() {
        int hash = 0;
        hash += (educationId != null ? educationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Education education = (Education) o;
        return Objects.equals(educationId, education.educationId);
    }

    @Override
    public String toString() {
        return name;
    }

}
