package com.qooco.boost.models.request.appointment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 3/25/2019 - 3:56 PM
*/
@Getter
@Setter
@NoArgsConstructor
public class AppointmentEventIdRemoveReq {
    private long[] ids;
    @ApiModelProperty(notes = "Fill value when change appointment")
    private Long targetId;
    @ApiModelProperty(notes = "Fill value when assign new appointment")
    private AppointmentReq appointment;
}
