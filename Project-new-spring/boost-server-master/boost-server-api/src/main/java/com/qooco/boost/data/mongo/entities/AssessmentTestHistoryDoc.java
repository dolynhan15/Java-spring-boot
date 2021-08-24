package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.data.mongo.embedded.assessment.AssessmentLevelEmbedded;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Document(collection = "AssessmentTestHistoryDoc")
@Setter @Getter @NoArgsConstructor @AllArgsConstructor
@Builder
@FieldNameConstants
public class AssessmentTestHistoryDoc {
    @Id
    private String id;
    private Long userProfileId;
    private Long assessmentId;
    private AssessmentLevelEmbedded level;
    private String assessmentName;
    private int minLevel;
    private int maxLevel;
    private String scaleId;
    private double score;
    private Long duration;
    private Date updatedDate;
    @NotNull
    private Date submissionTime;
    private Date updatedDateByItSelf;

    public AssessmentTestHistoryDoc(String scaleId) {
        this.scaleId = scaleId;
    }

    @Override
    public String toString() {
        return id;
    }
}
