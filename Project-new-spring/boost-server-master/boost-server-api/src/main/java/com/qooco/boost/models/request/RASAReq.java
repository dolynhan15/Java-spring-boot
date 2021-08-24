package com.qooco.boost.models.request;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class RASAReq {
    private String sender;
    private String message;
}