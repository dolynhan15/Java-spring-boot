package com.qooco.boost.data.mongo.embedded;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class ButtonEmbedded {
    private int id;
    private String name;
}
