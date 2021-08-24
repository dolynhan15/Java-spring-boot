package com.qooco.boost.models.request.message;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageReq implements Serializable {
    private String content;
}
