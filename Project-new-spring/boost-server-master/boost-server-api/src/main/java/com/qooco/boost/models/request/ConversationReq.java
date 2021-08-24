package com.qooco.boost.models.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor
public class ConversationReq {
    private Long vacancyId;
    private Long userProfileId;
}
