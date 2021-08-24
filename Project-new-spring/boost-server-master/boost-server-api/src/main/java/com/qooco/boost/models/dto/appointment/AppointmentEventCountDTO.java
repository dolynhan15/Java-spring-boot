package com.qooco.boost.models.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.models.dto.BaseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 11/26/2018 - 10:46 AM
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentEventCountDTO extends BaseDTO {
    private Long userProfileId;
    private Long companyId;
    private Long staffId;
    private Map<String, Integer> events;
}
