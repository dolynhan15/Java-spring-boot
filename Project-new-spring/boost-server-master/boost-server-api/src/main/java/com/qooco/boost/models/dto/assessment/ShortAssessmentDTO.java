package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.data.oracle.entities.Assessment;
import com.qooco.boost.models.dto.currency.CurrencyDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 12/10/2018 - 11:12 AM
*/
@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortAssessmentDTO {
    private Long id;
    private String name;
    private Double price;
    private String image;
    private Integer numberCompanyRequire;
    private CurrencyDTO currency;
    private String scaleId;
    private Long packageId;
    private Long topicId;
    private Long categoryId;
    @ApiModelProperty(notes = "Test duration limit in seconds" )
    private Long timeLimit;


    public static ShortAssessmentDTO init(Assessment assessment, String locale, String domainPath) {
        if (Objects.nonNull(assessment)) {
            return new ShortAssessmentDTO(assessment, locale, domainPath);
        }
        return null;
    }

    private ShortAssessmentDTO(Assessment assessment, String locale, String domainPath) {
        this.id = assessment.getId();
        this.name = assessment.getName();
        this.price = assessment.getPrice();
        this.scaleId = assessment.getScaleId();
        this.packageId = assessment.getPackageId();
        this.topicId = assessment.getTopicId();
        this.categoryId = assessment.getCategoryId();
        this.timeLimit = assessment.getTimeLimit();

        this.numberCompanyRequire = assessment.getNumberCompanyRequire();

        if (Objects.nonNull(domainPath) && Objects.nonNull(assessment.getPicture())) {
            this.image = domainPath.concat(assessment.getPicture());
        } else {
            this.image = assessment.getPicture();
        }
        switch (locale) {
            case QoocoApiConstants.LOCALE_ZH_CN:
                this.name = assessment.getName();
                break;
            case QoocoApiConstants.LOCALE_ZH_TW:
                this.name = assessment.getName();
                break;
            case QoocoApiConstants.LOCALE_ID_ID:
                this.name = assessment.getName();
                break;
            case QoocoApiConstants.LOCALE_JA_JP:
                this.name = assessment.getName();
                break;
            case QoocoApiConstants.LOCALE_MS_MY:
                this.name = assessment.getName();
                break;
            case QoocoApiConstants.LOCALE_TH_TH:
                this.name = assessment.getName();
                break;
            case QoocoApiConstants.LOCALE_VI_VN:
                this.name = assessment.getName();
                break;
            case QoocoApiConstants.LOCALE_KO_KR:
                this.name = assessment.getName();
                break;
            default:
                this.name = assessment.getName();
                break;
        }
    }

}
