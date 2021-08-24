package com.qooco.boost.data.mongo.embedded.message;

import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class AuthorizationMessage extends MessageStatus {
    private Long id;
    private CompanyEmbedded company;
    private int responseStatus;

    public AuthorizationMessage(Long id, CompanyEmbedded company) {
        this.id = id;
        this.company = company;
    }

    public AuthorizationMessage(AuthorizationMessage authorizationMessage) {
        super(authorizationMessage);
        if (Objects.nonNull(authorizationMessage)) {
            id = authorizationMessage.getId();
            responseStatus = authorizationMessage.getResponseStatus();
            if (Objects.nonNull(authorizationMessage.getCompany())) {
                company = new CompanyEmbedded(authorizationMessage.getCompany());
            }
        }
    }
}
