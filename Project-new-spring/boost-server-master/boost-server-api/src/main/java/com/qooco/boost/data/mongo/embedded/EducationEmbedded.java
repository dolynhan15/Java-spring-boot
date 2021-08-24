package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.Education;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class EducationEmbedded {
    private Long id;
    private String name;
    private String description;

    public EducationEmbedded(EducationEmbedded educationEmbedded) {
        if (Objects.nonNull(educationEmbedded)) {
            id = educationEmbedded.getId();
            name = educationEmbedded.getName();
            description = educationEmbedded.getDescription();
        }
    }

    public EducationEmbedded(Education education) {
        if (Objects.nonNull(education)) {
            this.id = education.getEducationId();
            this.name = education.getName();
            this.description = education.getDescription();
        }
    }
}
