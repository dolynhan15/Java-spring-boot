package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.assessment.AssessmentLevelEmbedded;
import com.qooco.boost.data.oracle.entities.AssessmentLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: tnlong
 Date: 7/10/2018 - 1:27 PM
*/
@Setter @Getter @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentLevelDTO {
    private Long id;
    private String name;
    private Integer level;

    public AssessmentLevelDTO(AssessmentLevel assessmentLevel) {
        if (Objects.nonNull(assessmentLevel)) {
            id = assessmentLevel.getId();
            name = assessmentLevel.getLevelName();
            level = NumberUtils.toInt(assessmentLevel.getAssessmentLevel());
        }
    }

    public AssessmentLevelDTO(AssessmentLevelEmbedded assessmentLevelEmbedded) {
        if (Objects.nonNull(assessmentLevelEmbedded)) {
            id = assessmentLevelEmbedded.getLevelId();
            name = assessmentLevelEmbedded.getLevelName();
            level = assessmentLevelEmbedded.getAssessmentLevel();
        }
    }
}
