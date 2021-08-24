package com.qooco.boost.data.mongo.embedded.message;

import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class AppliedMessage extends MessageStatus {

    private VacancyEmbedded vacancy;
    private int responseStatus;
    private boolean isAvailable;

    public  AppliedMessage(VacancyEmbedded vacancy) {
        this.vacancy = vacancy;
        this.isAvailable = true;
        this.responseStatus = 0;
    }

    public AppliedMessage(AppliedMessage appliedMessage) {
        if (Objects.nonNull(appliedMessage)) {
            this.responseStatus = appliedMessage.getResponseStatus();
            this.isAvailable = appliedMessage.isAvailable();
            if (Objects.nonNull(appliedMessage.getVacancy())) {
                vacancy = new VacancyEmbedded(appliedMessage.getVacancy());
            }
        }
    }
}
