package com.qooco.boost.models.request;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Min;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class DurationRequest {
    @Min(0)
    @Getter
    @Setter
    private long startDate;

    @Min(0)
    @Getter
    @Setter
    private long endDate;
}
