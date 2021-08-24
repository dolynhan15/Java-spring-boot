package com.qooco.boost.models.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class StatisticResp {
    private long viewsPerWeek;
    private long viewsPerMonth;
}