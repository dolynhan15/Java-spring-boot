package com.qooco.boost.data.mongo.embedded.assessment;

import com.qooco.boost.data.mongo.embedded.AssessmentEmbedded;
import lombok.*;

import java.util.Date;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QualificationEmbedded {
    private Long id;
    private Date submissionTime;
    private AssessmentEmbedded assessment;
    private AssessmentLevelEmbedded level;
}
