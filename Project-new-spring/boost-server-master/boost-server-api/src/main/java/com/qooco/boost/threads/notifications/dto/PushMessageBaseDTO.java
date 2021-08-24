package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.mongo.entities.base.MessageBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;


@Setter
@Getter
@NoArgsConstructor
public class PushMessageBaseDTO extends PushBaseDTO {
    private String id;
    private String conversationId;
    private String messageCenterId;
    private int messageCenterType;
    private UserProfileDTO sender;
    private int type;
    private int status;
    private Date createdDate;

    public PushMessageBaseDTO(MessageBase messageDoc) {
        super();
        this.id = messageDoc.getId().toHexString();
        this.conversationId = Objects.nonNull(messageDoc.getConversationId()) ? messageDoc.getConversationId().toHexString() : "";
        this.messageCenterId = Objects.nonNull(messageDoc.getMessageCenterId()) ? messageDoc.getMessageCenterId().toHexString() : "";
        this.messageCenterType = messageDoc.getMessageCenterType();
        this.sender = new UserProfileDTO(messageDoc.getSender());
        this.status = messageDoc.getStatus();
        this.type = messageDoc.getType();
        this.createdDate = messageDoc.getCreatedDate();
    }
}
