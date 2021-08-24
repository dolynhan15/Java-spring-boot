package com.qooco.boost.models.request.appointment;

import com.qooco.boost.data.oracle.entities.Appointment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class AppointmentRemoveReq {
    private Long deleteId;
    @ApiModelProperty(notes = "Fill value when change appointment")
    private Long targetId;
    @ApiModelProperty(notes = "Fill value when assign new appointment")
    private AppointmentReq appointment;

    public Appointment updateEntity(Appointment appointment) {
        if (Objects.nonNull(this.appointment)) {
            return this.appointment.updateEntity(appointment);
        }
        return null;
    }
}
