/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qooco.boost.data.oracle.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author mhvtrung
 */
@Entity
@Table(name = "VACANCY_LANGUAGE")
public class VacancyLanguage extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VACANCY_LANGUAGE_SEQUENCE")
    @SequenceGenerator(sequenceName = "VACANCY_LANGUAGE_SEQ", allocationSize = 1, name = "VACANCY_LANGUAGE_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "VACANCY_LANGUAGE_ID", nullable = false)
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "IS_NATIVE")
    private Boolean isNative;

    @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "LANGUAGE_ID")
    @ManyToOne(optional = false)
    private Language language;

    @JoinColumn(name = "VACANCY_ID", referencedColumnName = "VACANCY_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Vacancy vacancy;

    public VacancyLanguage() {
    }

    public VacancyLanguage(Long id) {
        this.id = id;
    }

    public VacancyLanguage(Boolean isNative, Language language, Vacancy vacancy, Long ownerId) {
        super(ownerId);
        this.isNative = isNative;
        this.language = language;
        this.vacancy = vacancy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isNative() {
        return isNative;
    }

    public void setNative(Boolean aNative) {
        isNative = aNative;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
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
        VacancyLanguage that = (VacancyLanguage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return language.toString() + vacancy.toString();
    }
}
