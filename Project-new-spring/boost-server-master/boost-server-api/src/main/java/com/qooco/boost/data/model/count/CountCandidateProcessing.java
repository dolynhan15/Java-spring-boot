package com.qooco.boost.data.model.count;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CountCandidateProcessing {

    private Date countOnDate;
    private int appliedCandidates;
    private int rejectedCandidates;

    public CountCandidateProcessing(Date countOnDate, int appliedCandidates, int rejectedCandidates) {
        this.countOnDate = countOnDate;
        this.appliedCandidates = appliedCandidates;
        this.rejectedCandidates = rejectedCandidates;
    }
}
