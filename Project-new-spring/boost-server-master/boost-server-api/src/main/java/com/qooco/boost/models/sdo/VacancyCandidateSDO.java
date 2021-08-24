package com.qooco.boost.models.sdo;

import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.oracle.entities.Vacancy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class VacancyCandidateSDO {
    private Long candidateId;
    private Vacancy vacancy;
    private VacancyDoc vacancyDoc;

    public VacancyCandidateSDO(long candidateId, Vacancy vacancy) {
        this.candidateId = candidateId;
        this.vacancy = vacancy;
    }
}
