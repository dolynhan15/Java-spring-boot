package com.qooco.boost.data.model;

import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.data.oracle.entities.Vacancy;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VacancyHasAppointment {

    private long id;
    private Vacancy vacancy;
    private long countAppointedCandidates;
    private List<UserCurriculumVitae> appointedCandidates;

    public VacancyHasAppointment(long id, Vacancy vacancy, long countAppointedCandidates) {
        this.id = id;
        this.vacancy = vacancy;
        this.countAppointedCandidates = countAppointedCandidates;
    }
}
