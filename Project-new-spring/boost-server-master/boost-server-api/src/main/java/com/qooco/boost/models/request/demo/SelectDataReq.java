package com.qooco.boost.models.request.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SelectDataReq {
    //1 Admin, 1 Head recruiter, 4 Recruiter: Create manual.
    @JsonIgnore
    private long companyId;
    private int[] openSeatRanges;
    private int[] closedSeatRanges;
    private int[] candidateProcessRanges;
    private long startDate;
    private long endDate;
}
