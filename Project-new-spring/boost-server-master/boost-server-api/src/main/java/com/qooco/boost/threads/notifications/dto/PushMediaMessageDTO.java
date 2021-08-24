package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.models.dto.message.MediaMessageDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
public class PushMediaMessageDTO extends PushMessageBaseDTO {
    private MediaMessageDTO mediaMessage;

    public PushMediaMessageDTO(MessageBase message) {
        super(message);
        ofNullable(message.getFileMessage()).ifPresent(it -> this.mediaMessage = new MediaMessageDTO(it));
    }
}
