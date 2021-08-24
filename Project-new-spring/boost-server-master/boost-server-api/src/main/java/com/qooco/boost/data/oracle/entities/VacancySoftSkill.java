/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qooco.boost.data.oracle.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author mhvtrung
 */
@Entity
@Table(name = "VACANCY_SOFT_SKILL")
public class VacancySoftSkill extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VACANCY_SOFT_SKILL_SEQUENCE")
    @SequenceGenerator(sequenceName = "VACANCY_SOFT_SKILL_SEQ", allocationSize = 1, name = "VACANCY_SOFT_SKILL_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "VACANCY_SOFT_SKILL_ID", nullable = false)
    private Long id;

    @JoinColumn(name = "SOFT_SKILL_ID", referencedColumnName = "SOFT_SKILL_ID")
    @ManyToOne(optional = false)
    private SoftSkill softSkill;

    @JoinColumn(name = "VACANCY_ID", referencedColumnName = "VACANCY_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Vacancy vacancy;

    public VacancySoftSkill() {
    }

    public VacancySoftSkill(Long id) {
        this.id = id;
    }
    public VacancySoftSkill(SoftSkill softSkill, Vacancy vacancy, Long ownerId) {
        super(ownerId);
        this.softSkill = softSkill;
        this.vacancy = vacancy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SoftSkill getSoftSkill() {
        return softSkill;
    }

    public void setSoftSkill(SoftSkill softSkill) {
        this.softSkill = softSkill;
    }

    public Vacancy getVacancy() {
        return vacancy;
    }

    public void setVacancy(Vacancy vacancy) {
        this.vacancy = vacancy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancySoftSkill that = (VacancySoftSkill) o;
        return Objects.equals(softSkill, that.softSkill) &&
                Objects.equals(vacancy, that.vacancy);
    }

    @Override
    public int hashCode() {

        return Objects.hash(softSkill, vacancy);
    }

    @Override
    public String toString() {
        return vacancy.toString() + softSkill.toString();
    }
}
