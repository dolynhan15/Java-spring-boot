package com.qooco.boost.data.utils;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MatchingCandidateConfig {

    public static final int IGNORE_SALARY = 1;
    public static final int IGNORE_QUALIFICATION = 2;
    public static final int EXPAND_SEARCH_RANGE = 3;
    public static final int LOWER_QUALIFICATION = 4;

    private int vacancyJobAlphaTest;
    private boolean boostScoreEnabled;
    private int expiredDays;
}
