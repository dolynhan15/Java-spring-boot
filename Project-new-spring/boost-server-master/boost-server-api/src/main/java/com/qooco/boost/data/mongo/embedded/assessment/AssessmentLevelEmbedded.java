package com.qooco.boost.data.mongo.embedded.assessment;

import com.qooco.boost.data.oracle.entities.AssessmentLevel;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentLevelEmbedded {
    private Long levelId;
    private String scaleId;
    private String mappingId;
    private String levelName;
    private String levelDescription;
    private Integer assessmentLevel;

    public AssessmentLevelEmbedded(AssessmentLevel level) {
        if (Objects.nonNull(level)) {
            this.levelId = level.getId();
            this.scaleId = level.getScaleId();
            this.mappingId = level.getMappingId();
            this.levelName = level.getLevelName();
            this.levelDescription = level.getLevelDescription();
            this.assessmentLevel = Integer.valueOf(level.getAssessmentLevel());
        }
    }
}