package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.message.AppliedMessage;
import com.qooco.boost.data.mongo.embedded.message.MessageStatus;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppliedMessageDTO extends MessageStatus {
    @Getter
    @Setter
    private VacancyShortInformationDTO vacancy;
    @Getter
    @Setter
    private int responseStatus;

    public AppliedMessageDTO(AppliedMessage appliedMessage, String locale) {
        if (Objects.nonNull(appliedMessage)) {
            this.vacancy = new VacancyShortInformationDTO(appliedMessage.getVacancy(), locale);
            this.responseStatus = appliedMessage.getResponseStatus();
            setStatus(appliedMessage.getStatus());
        }
    }
}
