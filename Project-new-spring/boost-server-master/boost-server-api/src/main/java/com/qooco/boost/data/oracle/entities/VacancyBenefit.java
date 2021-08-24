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
@Table(name = "VACANCY_BENEFIT")
public class VacancyBenefit extends BaseEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VACANCY_BENEFIT_SEQUENCE")
    @SequenceGenerator(sequenceName = "VACANCY_BENEFIT_SEQ", allocationSize = 1, name = "VACANCY_BENEFIT_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "VACANCY_BENEFIT_ID", nullable = false)
    private Long id;

    @JoinColumn(name = "BENEFIT_ID", referencedColumnName = "BENEFIT_ID")
    @ManyToOne(optional = false)
    private Benefit benefit;

    @JoinColumn(name = "VACANCY_ID", referencedColumnName = "VACANCY_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Vacancy vacancy;

    public VacancyBenefit() {
        super();
    }

    public VacancyBenefit(Long id) {
        this.id = id;
    }

    public VacancyBenefit(Benefit benefit, Vacancy vacancy, Long ownerId) {
        super(ownerId);
        this.benefit = benefit;
        this.vacancy = vacancy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Benefit getBenefit() {
        return benefit;
    }

    public void setBenefit(Benefit benefit) {
        this.benefit = benefit;
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
        VacancyBenefit that = (VacancyBenefit) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return vacancy.toString() + benefit.toString();
    }
    
}
