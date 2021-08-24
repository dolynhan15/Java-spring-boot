package com.qooco.boost.models.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 11/28/2018 - 1:28 PM
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManagerDTO {
    private Long staffId;
    private Long userProfileId;
    private String firstName;
    private String lastName;
    private String username;
    private String avatar;
    private Long companyId;

    public ManagerDTO(Staff staff) {
        if (Objects.nonNull(staff)) {
            this.staffId = staff.getStaffId();
            if (Objects.nonNull(staff.getCompany())) {
                this.companyId = staff.getCompany().getCompanyId();
            }
            if (Objects.nonNull(staff.getUserFit())) {
                this.userProfileId = staff.getUserFit().getUserProfileId();
                this.username = staff.getUserFit().getUsername();
                this.firstName = staff.getUserFit().getFirstName();
                this.lastName = staff.getUserFit().getLastName();
                this.avatar =  ServletUriUtils.getAbsolutePath(staff.getUserFit().getAvatar());
            }
        }
    }

    public ManagerDTO(StaffShortEmbedded manager) {
        if (Objects.nonNull(manager)) {
            this.staffId = manager.getId();
            if (Objects.nonNull(manager.getUserProfile())) {
                this.userProfileId = manager.getUserProfile().getUserProfileId();
                this.username = manager.getUserProfile().getUsername();
                this.firstName = manager.getUserProfile().getFirstName();
                this.lastName = manager.getUserProfile().getLastName();
                this.avatar = ServletUriUtils.getAbsolutePath(manager.getUserProfile().getAvatar());
            }
        }
    }
}
