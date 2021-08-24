package com.qooco.boost.models.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.models.dto.BaseDTO;
import com.qooco.boost.models.dto.user.BaseUserCVDTO;
import com.qooco.boost.utils.Converter;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentDetailDTO extends BaseDTO {
    @Setter @Getter
    private Long id;
    @Setter @Getter
    private BaseUserCVDTO candidate;
    @Setter @Getter
    private Date eventTime;
    private boolean isLocked;
    @Setter @Getter
    private int status;

    public AppointmentDetailDTO(AppointmentDetail appointmentDetail, String locale) {
        if (Objects.nonNull(appointmentDetail)) {
            this.id = appointmentDetail.getId();
            this.candidate = new BaseUserCVDTO(appointmentDetail.getUserCurriculumVitae(), locale);
            if (Objects.nonNull(appointmentDetail.getAppointmentTime())) {
                this.eventTime = DateUtils.getUtcForOracle(appointmentDetail.getAppointmentTime());
            }
            this.isLocked = false;
            this.status = AppointmentStatus.getStatusValue(Converter.valueOfInteger(appointmentDetail.getStatus(), 0));
            this.setUpdatedDate(DateUtils.getUtcForOracle(appointmentDetail.getUpdatedDate()));
        }
    }

    public AppointmentDetailDTO(AppointmentDetail appointmentDetail, boolean isLocked, String locale) {
        this(appointmentDetail, locale);
        this.isLocked = isLocked;
    }

    public boolean isLocked() {
        return isLocked;
    }

    @JsonProperty("isLocked")
    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
