package com.qooco.boost.data.model;

import com.qooco.boost.data.mongo.embedded.assessment.QualificationEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class UserCvDocQualificationAggregation {
    private Long id;
    private QualificationEmbedded qualifications;
}
