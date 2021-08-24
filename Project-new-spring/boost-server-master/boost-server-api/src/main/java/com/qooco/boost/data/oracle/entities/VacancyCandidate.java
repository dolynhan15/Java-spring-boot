package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "VACANCY_CANDIDATE")
public class VacancyCandidate extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VACANCY_CANDIDATE_SEQUENCE")
    @SequenceGenerator(sequenceName = "VACANCY_CANDIDATE_SEQ", allocationSize = 1, name = "VACANCY_CANDIDATE_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Long id;

    @JoinColumn(name = "CANDIDATE_ID", referencedColumnName = "CURRICULUM_VITAE_ID")
    @ManyToOne(optional = false)
    private UserCurriculumVitae candidate;

    @JoinColumn(name = "VACANCY_ID", referencedColumnName = "VACANCY_ID")
    @ManyToOne(optional = false)
    private Vacancy vacancy;

    @JoinColumn(name = "ARCHIVIST_ID", referencedColumnName = "STAFF_ID")
    @ManyToOne(optional = false)
    private Staff archivist;

    @Column(name = "CANDIDATE_STATUS", nullable = false)
    private int status;

    public VacancyCandidate(UserCurriculumVitae candidate, Vacancy vacancy, Staff archivist, int recruited) {
        super(archivist.getUserFit().getUserProfileId());
        this.candidate = candidate;
        this.vacancy = vacancy;
        this.archivist = archivist;
        this.status = recruited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancyCandidate vacancyCandidate = (VacancyCandidate) o;
        return Objects.equals(id, vacancyCandidate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return candidate.toString();
    }
}
