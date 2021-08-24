/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qooco.boost.data.oracle.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author mhvtrung
 */
@Entity
@Table(name = "VACANCY_DESIRED_HOUR")
public class VacancyDesiredHour extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VACANCY_DESIRED_HOUR_SEQUENCE")
    @SequenceGenerator(sequenceName = "VACANCY_DESIRED_HOUR_SEQ", allocationSize = 1, name = "VACANCY_DESIRED_HOUR_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "VACANCY_DESIRED_HOUR_ID", nullable = false)
    private Long id;

    @JoinColumn(name = "VACANCY_ID", referencedColumnName = "VACANCY_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Vacancy vacancy;

    @JoinColumn(name = "WORKING_HOUR_ID", referencedColumnName = "WORKING_HOUR_ID")
    @ManyToOne(optional = false)
    private WorkingHour workingHour;

    public VacancyDesiredHour() {
    }

    public VacancyDesiredHour(Long id) {
        super();
        this.id = id;
    }

    public VacancyDesiredHour(WorkingHour workingHour, Vacancy vacancy, Long ownerId) {
        super(ownerId);
        this.vacancy = vacancy;
        this.workingHour = workingHour;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vacancy getVacancy() {
        return vacancy;
    }

    public void setVacancy(Vacancy vacancy) {
        this.vacancy = vacancy;
    }

    public WorkingHour getWorkingHour() {
        return workingHour;
    }

    public void setWorkingHour(WorkingHour workingHour) {
        this.workingHour = workingHour;
    }
}
