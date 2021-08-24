package com.qooco.boost.models.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
public class ClaimCodeReq {
    private List<Long> assessmentIdList;

    public ClaimCodeReq(List<Long> assessmentIdList) {
        this.assessmentIdList = assessmentIdList;
    }
}
