package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.data.oracle.entities.PersonalAssessmentQuality;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 11:15 AM
*/
@Setter @Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalAssessmentQualityDTO {
    private Long id;
    private Long personalAssessmentId;
    private String name;
    private int qualityType;
    private int defaultValue;

    public PersonalAssessmentQualityDTO(PersonalAssessmentQuality assessmentQuality, String locale) {
        if (Objects.nonNull(assessmentQuality)) {
            id = assessmentQuality.getId();
            if (Objects.nonNull(assessmentQuality.getPersonalAssessment())) {
                personalAssessmentId = assessmentQuality.getPersonalAssessment().getId();
            }
            qualityType = assessmentQuality.getQualityType();
            defaultValue = assessmentQuality.getDefaultValue();
            switch (locale) {
                case QoocoApiConstants.LOCALE_ZH_CN:
                    this.name = assessmentQuality.getNameEnUs();
                    break;
                case QoocoApiConstants.LOCALE_ZH_TW:
                    this.name = assessmentQuality.getNameZhTw();
                    break;
                case QoocoApiConstants.LOCALE_ID_ID:
                    this.name = assessmentQuality.getNameIdId();
                    break;
                case QoocoApiConstants.LOCALE_JA_JP:
                    this.name = assessmentQuality.getNameJaJp();
                    break;
                case QoocoApiConstants.LOCALE_MS_MY:
                    this.name = assessmentQuality.getNameMsMy();
                    break;
                case QoocoApiConstants.LOCALE_TH_TH:
                    this.name = assessmentQuality.getNameThTh();
                    break;
                case QoocoApiConstants.LOCALE_VI_VN:
                    this.name = assessmentQuality.getNameViVn();
                    break;
                case QoocoApiConstants.LOCALE_KO_KR:
                    this.name = assessmentQuality.getNameKoKr();
                    break;
                default:
                    this.name = assessmentQuality.getNameEnUs();
                    break;
            }
        }
    }
}
