package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/26/2018 - 3:22 PM
*/
@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPersonalityResultDTO {
    private PersonalAssessmentDTO personalAssessment;
    private List<PersonalAssessmentQualityResultDTO> results;
}
