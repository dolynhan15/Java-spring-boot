package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class PushVacancyMessageDTO extends PushMessageBaseDTO {
    private VacancyDTO vacancy;

    public PushVacancyMessageDTO(MessageBase message, String locale) {
        super(message);
        if(message instanceof MessageDoc){
            if (Objects.nonNull(message.getAppliedMessage())) {
                this.vacancy = new VacancyDTO(message.getAppliedMessage(), locale);
            } else if(Objects.nonNull(message.getVacancyMessage())) {
                this.vacancy = new VacancyDTO(message.getVacancyMessage(), locale);
            }
        }
    }
}
