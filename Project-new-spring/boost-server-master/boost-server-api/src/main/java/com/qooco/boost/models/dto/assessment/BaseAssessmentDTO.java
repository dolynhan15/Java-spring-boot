package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.mongo.embedded.AssessmentEmbedded;
import com.qooco.boost.data.oracle.entities.Assessment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 12/10/2018 - 11:12 AM
*/
@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseAssessmentDTO {
    private Long id;
    private String scaleId;
    private Long packageId;
    private Long topicId;
    private Long categoryId;
    @ApiModelProperty(notes = "Test duration limit in seconds" )
    private Long timeLimit;


    public BaseAssessmentDTO(Assessment assessment) {
        this.id = assessment.getId();
        this.scaleId = assessment.getScaleId();
        this.packageId = assessment.getPackageId();
        this.topicId = assessment.getTopicId();
        this.categoryId = assessment.getCategoryId();
        this.timeLimit = assessment.getTimeLimit();
    }

    public BaseAssessmentDTO(AssessmentEmbedded assessment) {
        this.id = assessment.getId();
        this.scaleId = assessment.getScaleId();
        this.packageId = assessment.getPackageId();
        this.topicId = assessment.getTopicId();
        this.categoryId = assessment.getCategoryId();
        this.timeLimit = Constants.DEFAULT_TIME_FOR_ASSESSMENT_IN_SECOND;
    }

}
