package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.PersonalAssessmentQuestion;
import com.qooco.boost.enumeration.ApplicationContextHolder;
import com.qooco.boost.localization.PersonalAssessmentQuestionDatabaseMessageSource;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 11:13 AM
*/
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalAssessmentQuestionDTO {
    private Long id;
    private String content;
    @ApiModelProperty(notes = "1: String, 2: Image")
    private int questionType;
    @ApiModelProperty(notes = "1: Extroversion, 2: Agreeableness, 3: Conscientiousness, 4: Neuroticism, 5: Openness, 6: Empathetic")
    private int qualityType;
    private int minValue;
    private int maxValue;
    private boolean isReversed;
    @ApiModelProperty(notes = "-1 or 1: multiple with value to get real value")
    private int valueRate;

    public PersonalAssessmentQuestionDTO(PersonalAssessmentQuestion personalAssessmentQuestion, String locale) {
        if (Objects.nonNull(personalAssessmentQuestion)) {
            this.id = personalAssessmentQuestion.getId();
            this.questionType = personalAssessmentQuestion.getQuestionType();
            this.minValue = personalAssessmentQuestion.getMinValue();
            this.maxValue = personalAssessmentQuestion.getMaxValue();
            this.isReversed = personalAssessmentQuestion.isReversed();
            this.qualityType = personalAssessmentQuestion.getQualityType();
            this.valueRate = personalAssessmentQuestion.getValueRate();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.content = ctx.getBean(PersonalAssessmentQuestionDatabaseMessageSource.class).getMessage(personalAssessmentQuestion.getId().toString(), locale);
            }
            if (StringUtils.isBlank(content)) {
                this.content = personalAssessmentQuestion.getContentEnUs();
            }
        }
    }
}
