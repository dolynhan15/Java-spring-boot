package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.mongo.embedded.assessment.AssessmentLevelEmbedded;
import com.qooco.boost.data.oracle.entities.Assessment;
import lombok.*;

import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Optional.ofNullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "assessmentFullEmbeddedBuilder")
public class AssessmentFullEmbedded extends AssessmentEmbedded {
    private Long timeLimit;
    private List<AssessmentLevelEmbedded> levels;
    private Long userQualificationId;

    public AssessmentFullEmbedded(Assessment assessment, Long userQualificationId) {
        super(assessment);
        this.timeLimit = assessment.getTimeLimit();
        this.levels = ofNullable(assessment.getAssessmentLevels()).map(it -> it.stream().map(AssessmentLevelEmbedded::new).collect(toImmutableList())).orElse(null);
        this.userQualificationId = userQualificationId;
    }
}
