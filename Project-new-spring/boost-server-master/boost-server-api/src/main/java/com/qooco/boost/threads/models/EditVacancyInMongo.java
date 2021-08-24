package com.qooco.boost.threads.models;

import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.models.sdo.VacancyCandidateSDO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter @NoArgsConstructor
public class EditVacancyInMongo {
    private Vacancy vacancy;

    private Integer cancelReason;
    private CancelAppointmentDetailInMongo cancelAppointment;
    private VacancyCandidateSDO vacancyCandidate;

    public EditVacancyInMongo(Vacancy vacancy) {
        if (Objects.nonNull(vacancy)) {
            this.vacancy = vacancy;
        }
    }
}
