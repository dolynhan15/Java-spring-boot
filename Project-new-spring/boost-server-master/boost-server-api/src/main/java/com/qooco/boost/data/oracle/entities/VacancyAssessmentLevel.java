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
 * @author mhvtrung
 */
@Entity
@Table(name = "VACANCY_ASSESSMENT_LEVEL")
public class VacancyAssessmentLevel extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VACANCY_ASSESSMENT_LEVEL_SEQUENCE")
    @SequenceGenerator(sequenceName = "VACANCY_ASSESSMENT_LEVEL_SEQ", allocationSize = 1, name = "VACANCY_ASSESSMENT_LEVEL_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "VACANCY_ASSESSMENT_LEVEL_ID", nullable = false)
    private Long id;

    @JoinColumn(name = "ASSESSMENT_LEVEL_ID", referencedColumnName = "ASSESSMENT_LEVEL_ID")
    @ManyToOne()
    private AssessmentLevel assessmentLevel;

    @JoinColumn(name = "VACANCY_ID", referencedColumnName = "VACANCY_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Vacancy vacancy;

    public VacancyAssessmentLevel() {
    }

    public VacancyAssessmentLevel(Long id) {
        this.id = id;
    }

    public VacancyAssessmentLevel(AssessmentLevel assessmentLevel, Vacancy vacancy, Long ownerId) {
        super(ownerId);
        this.assessmentLevel = assessmentLevel;
        this.vacancy = vacancy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssessmentLevel getAssessmentLevel() {
        return assessmentLevel;
    }

    public void setAssessmentLevel(AssessmentLevel assessmentLevel) {
        this.assessmentLevel = assessmentLevel;
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
        VacancyAssessmentLevel that = (VacancyAssessmentLevel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return vacancy.toString() + assessmentLevel.toString();
    }
}
