package com.qooco.boost.models.dto.assessment;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 1:57 PM
*/

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.PersonalAssessment;
import com.qooco.boost.data.oracle.entities.PersonalAssessmentQuality;
import com.qooco.boost.data.oracle.entities.PersonalAssessmentQuestion;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalAssessmentFullDTO extends PersonalAssessmentOfUserDTO {

    private List<PersonalAssessmentQuestionDTO> questions;
    private List<PersonalAssessmentQualityDTO> qualities;

    public PersonalAssessmentFullDTO(PersonalAssessment personalAssessment, String locale, boolean isTested) {
        super(personalAssessment, locale, isTested);
        this.questions = new ArrayList<>();
        this.qualities = new ArrayList<>();
    }

    public PersonalAssessmentFullDTO(PersonalAssessment personalAssessment, String locale, boolean isTested,
                                     List<PersonalAssessmentQuestion> personalAssessmentQuestions) {
        this(personalAssessment, locale, isTested);
        questions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(personalAssessmentQuestions)) {
            personalAssessmentQuestions.forEach(q -> questions.add(new PersonalAssessmentQuestionDTO(q, locale)));
        }
    }

    public PersonalAssessmentFullDTO(PersonalAssessment personalAssessment, String locale, boolean isTested,
                                     List<PersonalAssessmentQuestion> personalAssessmentQuestions,
                                     List<PersonalAssessmentQuality> personalAssessmentQualities) {
        this(personalAssessment, locale, isTested, personalAssessmentQuestions);
        questions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(personalAssessmentQualities)) {
            personalAssessmentQualities.forEach(q -> qualities.add(new PersonalAssessmentQualityDTO(q, locale)));
        }
    }
}
