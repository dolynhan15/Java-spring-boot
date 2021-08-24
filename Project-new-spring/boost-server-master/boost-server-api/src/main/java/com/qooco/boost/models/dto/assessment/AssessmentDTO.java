package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.AssessmentFullEmbedded;
import com.qooco.boost.data.mongo.embedded.assessment.AssessmentLevelEmbedded;
import com.qooco.boost.data.oracle.entities.Assessment;
import com.qooco.boost.data.oracle.entities.AssessmentLevel;
import com.qooco.boost.models.dto.currency.CurrencyDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/*
* Copyright: Falcon Team - AxonActive
 User: tnlong
 Date: 7/10/2018 - 1:27 PM
*/
@Setter @Getter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentDTO {
    private Long id;
    private String name;
    private Double price;
    private String image;
    private Integer numberCompanyRequire;
    private List<AssessmentLevelDTO> levels;
    private CurrencyDTO currency;

    @ApiModelProperty(notes = "This file is deprecated" )
    @Deprecated
    private String picture;
    @ApiModelProperty(notes = "This file is deprecated" )
    @Deprecated
    private List<AssessmentLevelDTO> assessmentLevels;

    private String scaleId;
    private Long packageId;
    private Long topicId;
    private Long categoryId;
    @ApiModelProperty(notes = "Test duration limit in seconds" )
    private Long timeLimit;

    public AssessmentDTO(Assessment assessment, String domainPath) {
        this(assessment, domainPath, assessment.getAssessmentLevels());
    }

    public AssessmentDTO(Assessment assessment, String domainPath, List<AssessmentLevel> levels) {
        if (Objects.nonNull(assessment)) {
            this.id = assessment.getId();
            this.name = assessment.getName();
            this.price = assessment.getPrice();
            this.scaleId = assessment.getScaleId();
            this.packageId = assessment.getPackageId();
            this.topicId = assessment.getTopicId();
            this.categoryId = assessment.getCategoryId();
            this.timeLimit = assessment.getTimeLimit();

            this.numberCompanyRequire = assessment.getNumberCompanyRequire();
            if (CollectionUtils.isNotEmpty(levels)) {
                this.assessmentLevels = this.levels = createAssessmentLevelDTOS(levels);
            }

            if (Objects.nonNull(domainPath) && Objects.nonNull(assessment.getPicture())) {
                this.picture = this.image = domainPath.concat(assessment.getPicture());
            } else {
                this.picture = this.image = assessment.getPicture();
            }
        }
    }

    public AssessmentDTO(AssessmentFullEmbedded embedded, String domainPath) {
        if (Objects.nonNull(embedded)) {
            this.id = embedded.getId();
            this.name = embedded.getName();
            this.price = embedded.getPrice();
            this.scaleId = embedded.getScaleId();
            this.packageId = embedded.getPackageId();
            this.topicId = embedded.getTopicId();
            this.categoryId = embedded.getCategoryId();
            this.timeLimit = embedded.getTimeLimit();

            this.numberCompanyRequire = embedded.getNumberCompanyRequire();
            if (CollectionUtils.isNotEmpty(embedded.getLevels())) {
                this.assessmentLevels = this.levels = createAssessmentLevel(embedded.getLevels());
            }

            if (Objects.nonNull(domainPath) && Objects.nonNull(embedded.getPicture())) {
                this.picture = this.image = domainPath.concat(embedded.getPicture());
            } else {
                this.picture = this.image = embedded.getPicture();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessmentDTO that = (AssessmentDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private List<AssessmentLevelDTO> createAssessmentLevelDTOS(List<AssessmentLevel> assessmentLevels){
        return assessmentLevels.stream().map(AssessmentLevelDTO::new).collect(Collectors.toList());
    }

    private List<AssessmentLevelDTO> createAssessmentLevel(List<AssessmentLevelEmbedded> levels) {
        return levels.stream().map(AssessmentLevelDTO::new).collect(Collectors.toList());
    }
}
