package com.qooco.boost.data.model.count;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class LongCount{
    private Long id;
    private long total;
}
