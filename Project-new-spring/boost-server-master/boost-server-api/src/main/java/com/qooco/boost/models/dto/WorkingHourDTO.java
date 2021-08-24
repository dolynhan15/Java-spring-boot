package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qooco.boost.data.mongo.embedded.WorkingHourEmbedded;
import com.qooco.boost.data.oracle.entities.WorkingHour;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/10/2018 - 1:35 PM
*/
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkingHourDTO {
    @Getter @Setter
    private Long workingHourId;
    @Getter @Setter
    private String workingHourDescription;
    @Getter @Setter
    private String startTime;
    @Getter @Setter
    private String endTime;
    private boolean isFullTime;

    public WorkingHourDTO(WorkingHour workingHour) {
        if (Objects.nonNull(workingHour)) {
            this.workingHourId = workingHour.getWorkingHourId();
            this.workingHourDescription = workingHour.getWorkingHourDescription();
            this.startTime = workingHour.getStartDate();
            this.endTime = workingHour.getEndDate();
            this.isFullTime = workingHour.isWorkingType();
        }
    }

    public WorkingHourDTO(WorkingHourEmbedded workingHour) {
        if (Objects.nonNull(workingHour)) {
            this.workingHourId = workingHour.getId();
            this.workingHourDescription = workingHour.getDescription();
            this.startTime = workingHour.getStartDate();
            this.endTime = workingHour.getEndDate();
            this.isFullTime = workingHour.isFullTime();
        }
    }

    public boolean isFullTime() {
        return isFullTime;
    }

    @JsonProperty("isFullTime")
    public void setFullTime(boolean fullTime) {
        this.isFullTime = fullTime;
    }
}
