package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
public class PushJoinCompanyApprovalDTO extends PushMessageBaseDTO{
    private CompanyDTO company;
    private int responseStatus;
    private int messageAction;

    public PushJoinCompanyApprovalDTO(MessageBase messageDoc) {
        super(messageDoc);
        ofNullable(messageDoc.getAuthorizationMessage()).ifPresent(it -> {
            this.company = new CompanyDTO(it.getCompany());
            this.responseStatus = it.getResponseStatus();
        });
        this.messageAction = MessageConstants.UPDATE_MESSAGE_ACTION;
        this.setSender(new UserProfileDTO(messageDoc.getRecipient()));
    }
}
