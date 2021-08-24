package com.qooco.boost.models.qooco.sync.leveltest;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/25/2018 - 10:51 AM
*/
@Setter @Getter
public class LevelTest {
    private long topicId;
    private long packageId;
    private long categoryId;
    private String scaleId;
    @ApiModelProperty(notes = "Time limited for test (second)")
    private long timeLimit;
}
