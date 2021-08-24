package com.qooco.boost.data.mongo.embedded.message;

import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CongratulationMessage {
    private VacancyEmbedded vacancy;

    public CongratulationMessage(VacancyEmbedded vacancy) {
        if (Objects.nonNull(vacancy)) {
            this.vacancy = vacancy;
        }
    }
}
