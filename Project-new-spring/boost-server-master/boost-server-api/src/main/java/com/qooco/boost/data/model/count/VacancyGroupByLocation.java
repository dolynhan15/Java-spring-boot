package com.qooco.boost.data.model.count;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants(innerTypeName = "VacancyFields")
public class VacancyGroupByLocation extends CountByLocation {
    private List<Long> vacancyIds;
}
