package com.qooco.boost.models.qooco.sync.leveltestdata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/15/2018 - 10:55 AM
*/

@Setter @Getter @NoArgsConstructor
public class LevelTestData {

    private long userId;
    private long testId;
    private String scaleId;
    private Integer level;
    private int minLevel;
    private int maxLevel;
    private Long duration;
    private Date timestamp;
}
