package com.qooco.boost.models.dto.assessment;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/26/2018 - 3:28 PM
*/

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.PersonalAssessmentQuality;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalAssessmentQualityResultDTO extends PersonalAssessmentQualityDTO {
    private double score;
    private int totalScore;

    public PersonalAssessmentQualityResultDTO(PersonalAssessmentQuality assessmentQuality, String locale, double score, int totalScore) {
        super(assessmentQuality, locale);
        this.score = score;
        this.totalScore = totalScore;
    }
}
