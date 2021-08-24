package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.UserPersonality;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 11:13 AM
*/
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPersonalityDTO {
    private Long id;
    private PersonalAssessmentQuestionDTO personalAssessmentQuestion;
    private int answerValue;
    private int timeToAnswer;

    public UserPersonalityDTO(UserPersonality userPersonality, String locale) {
        if (Objects.nonNull(userPersonality)) {
            id = userPersonality.getId();
            answerValue = userPersonality.getAnswerValue();
            timeToAnswer = userPersonality.getTimeToAnswer();
            if (Objects.nonNull(userPersonality.getPersonalAssessmentQuestion())) {
                personalAssessmentQuestion = new PersonalAssessmentQuestionDTO(userPersonality.getPersonalAssessmentQuestion(), locale);
            }
        }
    }
}
