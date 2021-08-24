package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.oracle.entities.PersonalAssessment;
import com.qooco.boost.enumeration.ApplicationContextHolder;
import com.qooco.boost.localization.PersonalAssessmentDatabaseMessageSource;
import com.qooco.boost.utils.LocaleConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalAssessmentDTO {
    private Long id;
    private String name;
    private String fullName;
    private String description;
    private String codeName;
    @ApiModelProperty(notes = "1: Circle graph, 2: Radar graph")
    private int typeGraph;

    public PersonalAssessmentDTO(PersonalAssessment personalAssessment, String locale) {
        if (Objects.nonNull(personalAssessment)) {
            this.id = personalAssessment.getId();
            this.codeName = personalAssessment.getCodeName();
            this.typeGraph = personalAssessment.getTypeGraph();

            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(PersonalAssessmentDatabaseMessageSource.class)
                        .getMessage(personalAssessment.getId() + Constants.UNDER_SCORE + LocaleConstant.EXCEL_PERSONAL_ASSESSMENT_KEY_NAME, locale);
                this.fullName = ctx.getBean(PersonalAssessmentDatabaseMessageSource.class)
                        .getMessage(personalAssessment.getId() + Constants.UNDER_SCORE + LocaleConstant.EXCEL_PERSONAL_ASSESSMENT_KEY_FULL_NAME, locale);
                this.description = ctx.getBean(PersonalAssessmentDatabaseMessageSource.class)
                        .getMessage(personalAssessment.getId() + Constants.UNDER_SCORE + LocaleConstant.EXCEL_PERSONAL_ASSESSMENT_KEY_DESCRIPTION, locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = personalAssessment.getNameEnUs();
            }
            if (StringUtils.isBlank(fullName)) {
                this.fullName = personalAssessment.getFullNameEnUs();
            }
            if (StringUtils.isBlank(description)) {
                this.description = personalAssessment.getDescriptionEnUs();
            }
        }
    }
}
