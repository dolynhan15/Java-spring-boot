package com.qooco.boost.models.response;

import com.qooco.boost.models.dto.assessment.PersonalAssessmentDTO;
import com.qooco.boost.models.dto.assessment.PersonalAssessmentQuestionDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PersonalAssessmentQuestionResp {
    private List<PersonalAssessmentQuestionDTO> personalAssessmentQuestions;
    private PersonalAssessmentDTO personalAssessment;
}