package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.Assessment;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "assessmentEmbeddedBuilder", toBuilder = true)
public class AssessmentEmbedded {

    private Long id;
    private Integer type;
    private String name;
    private Double price;
    private String picture;
    private int numberCompanyRequire;

    private String scaleId;
    private String mappingId;
    private Long packageId;
    private Long topicId;
    private Long categoryId;


    public AssessmentEmbedded(Long id) {
        this.id = id;
    }

    public AssessmentEmbedded(Assessment assessment) {
        if (Objects.nonNull(assessment)) {
            this.id = assessment.getId();
            this.type = assessment.getType();
            this.name = assessment.getName();
            this.price = assessment.getPrice();
            this.picture = assessment.getPicture();
            this.numberCompanyRequire = assessment.getNumberCompanyRequire();
            this.scaleId = assessment.getScaleId();
            this.mappingId = assessment.getMappingId();
            this.packageId = assessment.getPackageId();
            this.topicId = assessment.getTopicId();
            this.categoryId = assessment.getCategoryId();
        }
    }
}
