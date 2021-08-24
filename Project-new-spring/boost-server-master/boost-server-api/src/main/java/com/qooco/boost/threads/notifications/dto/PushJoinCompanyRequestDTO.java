package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.mongo.entities.base.MessageBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
public class PushJoinCompanyRequestDTO extends PushMessageBaseDTO {
    private CompanyDTO company;

    public PushJoinCompanyRequestDTO(MessageBase messageDoc) {
        super(messageDoc);
        ofNullable(messageDoc.getAuthorizationMessage()).ifPresent(it -> this.company = new CompanyDTO(it.getCompany()));
    }
}
