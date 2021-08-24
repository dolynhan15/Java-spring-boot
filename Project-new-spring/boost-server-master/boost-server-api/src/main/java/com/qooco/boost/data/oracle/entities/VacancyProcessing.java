package com.qooco.boost.data.oracle.entities;

import com.qooco.boost.utils.DateUtils;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "VACANCY_PROCESSING")
@Builder
@AllArgsConstructor
public class VacancyProcessing implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VACANCY_PROCESSING_SEQUENCE")
    @SequenceGenerator(sequenceName = "VACANCY_PROCESSING_SEQ", allocationSize = 1, name = "VACANCY_PROCESSING_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Long id;

    @JoinColumn(name = "USER_CV_ID", referencedColumnName = "CURRICULUM_VITAE_ID")
    @ManyToOne(optional = false)
    private UserCurriculumVitae candidate;

    @JoinColumn(name = "VACANCY_ID", referencedColumnName = "VACANCY_ID")
    @ManyToOne(optional = false)
    private Vacancy vacancy;

    @JoinColumn(name = "STAFF_ID", referencedColumnName = "STAFF_ID")
    @ManyToOne(optional = false)
    private Staff staff;

    @Column(name = "PROCESS_TYPE", nullable = false)
    private int type; // APPLIED = 1; REJECTED = 2;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    public VacancyProcessing(UserCurriculumVitae candidate, Vacancy vacancy, Staff staff, int processType) {
        this.candidate = candidate;
        this.vacancy = vacancy;
        this.staff = staff;
        this.type = processType;
        this.createdDate = DateUtils.toUtcForOracle(new Date());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancyProcessing vacancyCandidate = (VacancyProcessing) o;
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
