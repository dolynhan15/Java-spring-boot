package com.qooco.boost.models.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.models.dto.JobDTO;
import com.qooco.boost.models.dto.user.CandidateInfoDTO;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
//TODO: refactor new suitable "AppointmentReminderDTO" name
public class AppointmentReminderDTO {
    private int count;
    private Date appointmentTime;
    private Date serverTime = new Date();
    private CandidateInfoDTO candidate;
    private JobDTO job;

    public AppointmentReminderDTO(List<AppointmentDetail> appointmentDetails, String locale) {
        if (CollectionUtils.isNotEmpty(appointmentDetails) && Objects.nonNull(appointmentDetails.get(0))
                && Objects.nonNull(appointmentDetails.get(0).getAppointmentTime())) {
            this.appointmentTime = DateUtils.getUtcForOracle(appointmentDetails.get(0).getAppointmentTime());
            this.candidate = new CandidateInfoDTO(appointmentDetails.get(0).getUserCurriculumVitae());
            if (Objects.nonNull(appointmentDetails.get(0).getAppointment()) && Objects.nonNull(appointmentDetails.get(0).getAppointment().getVacancy())) {
                this.job = new JobDTO(appointmentDetails.get(0).getAppointment().getVacancy().getJob(), locale);
            }
        }
        this.count = appointmentDetails.size();
    }

    public AppointmentReminderDTO(AppointmentDetail latestEvent, int count, String locale) {
        if (Objects.nonNull(latestEvent)) {
            if (Objects.nonNull(latestEvent.getAppointmentTime())) {
                this.appointmentTime = DateUtils.getUtcForOracle(latestEvent.getAppointmentTime());
            }
            if (Objects.nonNull(latestEvent.getAppointment()) && Objects.nonNull(latestEvent.getAppointment().getVacancy())) {
                this.job = new JobDTO(latestEvent.getAppointment().getVacancy().getJob(), locale);
            }
            this.candidate = new CandidateInfoDTO(latestEvent.getUserCurriculumVitae());
        }
        this.count = count;
    }
}
