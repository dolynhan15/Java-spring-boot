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
public class PushApplicantResponseDTO extends PushMessageBaseDTO {

    private int messageAction;
    private int responseStatus;

    public PushApplicantResponseDTO(MessageBase messageDoc) {
        super(messageDoc);
        ofNullable(messageDoc.getAppliedMessage()).ifPresent(it -> this.responseStatus = it.getResponseStatus());
        this.messageAction = MessageConstants.UPDATE_MESSAGE_ACTION;
        this.setSender(new UserProfileDTO(messageDoc.getRecipient()));
    }
}
