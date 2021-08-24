package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.utils.CipherKeys;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
public class PushMessageDTO extends PushMessageBaseDTO {
    private String message;
    private String content;
    private int messageAction;

    public PushMessageDTO(MessageBase message) {
        super(message);
        ofNullable(message.getContent()).ifPresent(it -> this.content = CipherKeys.encryptByAES(it, message.getSecretKey()));
        this.messageAction = MessageConstants.SUBMIT_MESSAGE_ACTION;
    }
}
