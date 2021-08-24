package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.mongo.embedded.message.BoostHelperMessage;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.utils.CipherKeys;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
public class PushBoostHelperMessageDTO extends PushMessageBaseDTO {
    private String text;
    private int eventType;
    private int messageAction;

    public PushBoostHelperMessageDTO(MessageBase messageDoc) {
        super((messageDoc));
        ofNullable(messageDoc.getBoostHelperMessage()).map(it -> CipherKeys.encryptByAES(it.getText(), messageDoc.getSecretKey())).ifPresent(this::setText);
        ofNullable(messageDoc.getBoostHelperMessage()).map(BoostHelperMessage::getEventType).ifPresent(this::setEventType);
        this.messageAction = MessageConstants.SUBMIT_MESSAGE_ACTION;
    }
}
