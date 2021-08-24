package com.qooco.boost.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qooco.boost.data.constants.SearchRange;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EditVacancyRequest {

    @JsonIgnore
    private long id;
    @ApiModelProperty(notes = "CITY:" + SearchRange.CITY +
            "PROVINCE: " + SearchRange.PROVINCE +
            "REGION: " + SearchRange.REGION +
            "COUNTRY: " + SearchRange.COUNTRY +
            "WORLD: " + SearchRange.WORLD)
    private Integer searchRange;
    private Integer numberOfSeat;
    private Double salary;
    private Double salaryMax;
    private Long[] assessmentLevelIds;
    private Long currencyId;

}
