package com.qooco.boost.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qooco.boost.data.oracle.entities.PersonalAssessmentQuestion;
import lombok.Getter;
import lombok.Setter;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 3:39 PM
*/
@Getter
public class PersonalAssessmentAnswer {
    private int answerValue;
    private int answerTime;

    @JsonIgnore
    @Getter
    @Setter
    private PersonalAssessmentQuestion personalAssessmentQuestion;
}
