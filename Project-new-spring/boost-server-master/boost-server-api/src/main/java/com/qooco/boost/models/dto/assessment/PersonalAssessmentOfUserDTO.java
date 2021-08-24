package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.PersonalAssessment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 11:13 AM
*/
@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalAssessmentOfUserDTO extends PersonalAssessmentDTO {
    private boolean isTested;

    public PersonalAssessmentOfUserDTO(PersonalAssessment personalAssessment, String locale, boolean isTested) {
        super(personalAssessment, locale);
        this.isTested = isTested;
    }
}
