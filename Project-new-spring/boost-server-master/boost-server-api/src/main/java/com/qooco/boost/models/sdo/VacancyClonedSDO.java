package com.qooco.boost.models.sdo;

import com.qooco.boost.data.oracle.entities.Vacancy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter @Getter @NoArgsConstructor
public class VacancyClonedSDO {
    private Long oldVacancyId;
    private Vacancy clonedVacancy;

    public VacancyClonedSDO(Vacancy vacancy, Long oldVacancyId) {
        if (Objects.nonNull(vacancy)) {
            this.clonedVacancy = vacancy;
        }
        this.oldVacancyId = oldVacancyId;
    }
}
