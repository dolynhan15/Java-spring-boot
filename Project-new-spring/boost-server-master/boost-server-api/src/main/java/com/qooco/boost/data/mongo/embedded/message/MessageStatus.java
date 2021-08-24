package com.qooco.boost.data.mongo.embedded.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class MessageStatus {
    private Integer status;

    public MessageStatus(MessageStatus messageStatus) {
        if (Objects.nonNull(messageStatus)) {
            this.status = messageStatus.getStatus();
        }
    }
}
