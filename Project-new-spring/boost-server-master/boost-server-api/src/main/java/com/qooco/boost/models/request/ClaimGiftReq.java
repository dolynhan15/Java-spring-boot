package com.qooco.boost.models.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
public class ClaimGiftReq {
    private List<Long> giftIds;
}
