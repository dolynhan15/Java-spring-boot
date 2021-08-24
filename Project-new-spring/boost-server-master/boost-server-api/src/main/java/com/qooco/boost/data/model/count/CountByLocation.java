package com.qooco.boost.data.model.count;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants(innerTypeName = "LocationFields")
public class CountByLocation {
    private int total;
    private Long cityId;
    private Long provinceId;
    private Long countryId;
}
