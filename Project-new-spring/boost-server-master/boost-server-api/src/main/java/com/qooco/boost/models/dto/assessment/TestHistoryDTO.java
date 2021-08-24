package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 10/1/2018 - 2:48 PM
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestHistoryDTO {
    private Date submissionTime;
    private Date expiredTime;
    private Long duration;
    private Integer level;

    public TestHistoryDTO(AssessmentTestHistoryDoc historyDoc, int expiredDay) {
        if (Objects.nonNull(historyDoc)) {
            this.duration = historyDoc.getDuration();
            this.submissionTime = historyDoc.getSubmissionTime();
            this.expiredTime = DateUtils.addDays(submissionTime, expiredDay);
            if (Objects.nonNull(historyDoc.getLevel())) {
                this.level = historyDoc.getLevel().getAssessmentLevel();
            }
        }
    }
}
