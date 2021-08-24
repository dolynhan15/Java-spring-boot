package com.qooco.boost.models.request.message;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class HistoryMessageReq implements Serializable {
    private String conversationId;
    private String timezone;
    private Date timestamp;
    private int size;
}
