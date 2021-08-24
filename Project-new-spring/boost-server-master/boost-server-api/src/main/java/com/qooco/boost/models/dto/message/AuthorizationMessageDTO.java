package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.message.AuthorizationMessage;
import com.qooco.boost.data.mongo.embedded.message.MessageStatus;
import com.qooco.boost.models.dto.company.CompanyShortInformationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizationMessageDTO extends MessageStatus {
    @Getter @Setter
    private Long id;
    @Getter @Setter
    private CompanyShortInformationDTO company;

    @Getter @Setter
    private int responseStatus;

    public AuthorizationMessageDTO(AuthorizationMessage authorizationMessage) {
        if (Objects.nonNull(authorizationMessage)) {
            id = authorizationMessage.getId();
            company = new CompanyShortInformationDTO(authorizationMessage.getCompany());
            responseStatus = authorizationMessage.getResponseStatus();
            setStatus(authorizationMessage.getStatus());
        }
    }
}
