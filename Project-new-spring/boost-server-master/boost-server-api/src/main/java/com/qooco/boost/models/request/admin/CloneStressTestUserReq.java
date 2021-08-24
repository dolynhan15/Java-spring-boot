package com.qooco.boost.models.request.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CloneStressTestUserReq {
    private long userProfileId;
    private long repeatNumber;
    private Long createdBy;
}
