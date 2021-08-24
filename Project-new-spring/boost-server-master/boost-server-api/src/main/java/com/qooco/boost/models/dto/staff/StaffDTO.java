package com.qooco.boost.models.dto.staff;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.models.dto.BaseDTO;
import com.qooco.boost.models.dto.RoleCompanyDTO;
import com.qooco.boost.models.dto.company.CompanyDTO;
import com.qooco.boost.models.dto.company.CompanyShortInformationDTO;
import com.qooco.boost.models.dto.user.UserFitDTO;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffDTO extends BaseDTO {
    private Long id;
    private UserFitDTO userProfile;
    private CompanyShortInformationDTO company;
    private RoleCompanyDTO roleCompany;

    public StaffDTO(Staff staff, String locale) {
        if (Objects.nonNull(staff)) {
            this.id = staff.getStaffId();
            this.company = new CompanyDTO(staff.getCompany(), locale);
            this.roleCompany = Objects.nonNull(staff.getRole()) ? new RoleCompanyDTO(staff.getRole(), locale) : null;
            //TODO: Should get FitUserDoc instead of UserProfile
            this.userProfile = Objects.nonNull(staff.getUserFit()) ? new UserFitDTO(staff.getUserFit(), locale) : null;
            setUpdatedDate(DateUtils.toUtcForOracle(staff.getUpdatedDate()));
        }
    }

    public StaffDTO(StaffEmbedded staff, String locale) {
        if (Objects.nonNull(staff)) {
            this.id = staff.getId();
            this.company = new CompanyDTO(staff.getCompany());
            this.roleCompany = Objects.nonNull(staff.getRoleCompany()) ? new RoleCompanyDTO(staff.getRoleCompany(), locale) : null;
            this.userProfile = Objects.nonNull(staff.getUserProfile()) ? new UserFitDTO(staff.getUserProfile(), locale) : null;
        }
    }
}
