package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateInfoDTO {
    private Long curriculumVitaeId;
    private String avatar;
    private String firstName;
    private String lastName;
    private String username;

    public CandidateInfoDTO(Long curriculumVitaeId, String avatar) {
        this.curriculumVitaeId = curriculumVitaeId;
        this.avatar = ServletUriUtils.getAbsolutePath(avatar);
    }

    public CandidateInfoDTO(AppointmentDetail appointmentDetail) {
        if (Objects.nonNull(appointmentDetail) && Objects.nonNull(appointmentDetail.getUserCurriculumVitae())) {
            this.curriculumVitaeId = appointmentDetail.getUserCurriculumVitae().getCurriculumVitaeId();
            if (Objects.nonNull(appointmentDetail.getUserCurriculumVitae().getUserProfile())) {
                this.avatar = ServletUriUtils.getAbsolutePath(appointmentDetail.getUserCurriculumVitae().getUserProfile().getAvatar());
            }
        }
    }

    public CandidateInfoDTO(UserCurriculumVitae userCV) {
        if (Objects.nonNull(userCV)) {
            this.curriculumVitaeId = userCV.getCurriculumVitaeId();
            if (Objects.nonNull(userCV.getUserProfile())) {
                this.avatar = ServletUriUtils.getAbsolutePath(userCV.getUserProfile().getAvatar());
                this.firstName = userCV.getUserProfile().getFirstName();
                this.lastName = userCV.getUserProfile().getLastName();
                this.username = userCV.getUserProfile().getUsername();
            }
        }
    }
}
