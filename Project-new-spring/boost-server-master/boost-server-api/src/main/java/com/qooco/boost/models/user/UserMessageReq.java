package com.qooco.boost.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class UserMessageReq {
    private String message;

    @JsonIgnore
    private String conversationId;
}
